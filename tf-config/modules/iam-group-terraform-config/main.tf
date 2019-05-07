resource "aws_iam_group" "terraform" {
  name = "${var.group_name}"
}

data "aws_iam_policy" "terraform-ec2" {
  arn = "arn:aws:iam::aws:policy/AmazonEC2FullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-ec2" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ec2.arn}"
}

# Multiple roles are added for ECS propose:
# - `AmazonEC2ContainerServiceforEC2Role`
# - `AmazonEC2ContainerServiceRole`
# - `AmazonECSTaskExecutionRolePolicy`
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs_managed_policies.html
data "aws_iam_policy" "terraform-ecs_container_instance" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs_container_instance" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs_container_instance.arn}"
}

# `AmazonECS_FullAccess` fully covers `AmazonEC2ContainerServiceRole`,
# and the following link suggest to use full access.
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_GetStarted.html
#
# TODO:
# If using EC2 launch type, then `AmazonEC2ContainerServiceRole` is enough.
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_GetStarted_EC2.html
data "aws_iam_policy" "terraform-ecs" {
  arn = "arn:aws:iam::aws:policy/AmazonECS_FullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs.arn}"
}

data "aws_iam_policy" "terraform-ecs_fargate" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs_fargate" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs_fargate.arn}"
}

resource "aws_iam_policy" "terraform-ecr" {
  name        = "AmazonECRConfigPolicy"
  path        = "/"

  # https://docs.aws.amazon.com/AmazonECR/latest/userguide/RepositoryPolicyExamples.html
  # `ecr:CreateRepository` and `ecr:ListTagsForResource` is used by `resource "aws_ecr_repository"`
  # `ecr:DescribeRepositories` is used by `data "aws_ecr_repository"`
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "DenyPull",
            "Effect": "Allow",
            "Action": [
                "ecr:CreateRepository",
                "ecr:DeleteRepository",
                "ecr:ListTagsForResource",
                "ecr:DescribeRepositories"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "terraform-ecr" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.terraform-ecr.arn}"
}

data "aws_iam_policy" "terraform-rds" {
  arn = "arn:aws:iam::aws:policy/AmazonRDSFullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-rds" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-rds.arn}"
}

# This is the service-linked role automatically created by `aws_ecs_service`
# https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html
# `AWSServiceRoleForECS` service role will be created automatically when we use
# `awsvpn` network mode, so there's no need to create it by ourselves.
data "aws_iam_role" "ecs_service" {
  name = "AWSServiceRoleForECS"

  # TODO:
  # It is right now in the setup (and will not be destroied by `terraform destroy`)
  # but may need some `dependency` in here for save.
}

resource "aws_iam_policy" "ecs_service_linked" {
  name        = "AWSServiceRoleForECSServiceLinkedPolicy"
  path        = "/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:CreateServiceLinkedRole"
            ],
            "Resource": "${data.aws_iam_role.ecs_service.arn}",
            "Condition": {"StringLike": {"iam:AWSServiceName": "ecs.amazonaws.com"}}
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "ecs_service_linked_attach" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.ecs_service_linked.arn}"
}

resource "aws_iam_role" "ecs_instance" {
  name                = "AmazonEC2ContainerServiceforEC2Role"
  path                = "/"
  assume_role_policy  = "${data.aws_iam_policy_document.ecs_instance.json}"
}

data "aws_iam_policy_document" "ecs_instance" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

data "aws_iam_policy" "ecs_instance" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_role_policy_attachment" "ecs_instance_attach" {
    role       = "${aws_iam_role.ecs_instance.name}"
    policy_arn = "${data.aws_iam_policy.ecs_instance.arn}"
}

resource "aws_iam_policy" "ecs_instance_role_linked" {
  name        = "AmazonEC2ContainerServiceforEC2RoleLinkedPolicy"
  path        = "/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:GetRole",
                "iam:PassRole"
            ],
            "Resource": "${aws_iam_role.ecs_instance.arn}"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "ecs_instance_role_linked_attach" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.ecs_instance_role_linked.arn}"
}

# This is a role which is used by the ECS tasks themselves.
resource "aws_iam_role" "ecs_task_execution" {
  name               = "AmazonECSTaskExecutionRole"
  path               = "/"
  assume_role_policy = "${data.aws_iam_policy_document.ecs_task_execution.json}"
}

data "aws_iam_policy_document" "ecs_task_execution" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

data "aws_iam_policy" "ecs_task_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_attach" {
  role       = "${aws_iam_role.ecs_task_execution.name}"
  policy_arn = "${data.aws_iam_policy.ecs_task_execution.arn}"
}

resource "aws_iam_policy" "ecs_task_execution_role_linked" {
  name        = "AWSServiceRoleForECSTaskRoleLinkedPolicy"
  path        = "/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:GetRole",
                "iam:PassRole"
            ],
            "Resource": "${aws_iam_role.ecs_task_execution.arn}"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "ecs_task_execution_role_linked_attach" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.ecs_task_execution_role_linked.arn}"
}

resource "aws_iam_policy" "instance_profile" {
  name        = "AmazonIAMInstanceProfilePolicy"
  path        = "/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:CreateInstanceProfile",
                "iam:GetInstanceProfile",
                "iam:RemoveRoleFromInstanceProfile",
                "iam:DeleteInstanceProfile",
                "iam:AddRoleToInstanceProfile"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "instance_profile_attach" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.instance_profile.arn}"
}
