# ALB Security Group: Edit this to restrict access to the application
resource "aws_security_group" "web_alb" {
  name = "${local.web_entrace_resource_name}"
  vpc_id      = "${aws_vpc.main.id}"

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = "${local.http_port}"
    to_port     = "${local.http_port}"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# A security group for the containers we will run EC2 instances used for ECS
# EC2 launch type. Two rules, allowing network traffic from a public facing load
# balancer and from other members of the security group.
#
# Remove any of the following ingress rules that are not needed.
# If you want to make direct requests to a container using its
# public IP address you'll need to add a security group rule
# to allow traffic from all IP addresses.
#
# Traffic to the ECS cluster should only come from the ALB
resource "aws_security_group" "web_app" {
  name = "${local.web_app_resource_name}"
  vpc_id      = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "web_app_ecs_tasks_egress" {
  type            = "egress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.web_app.id}"
}

resource "aws_security_group_rule" "web_app_ecs_tasks_lb_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = "${local.web_app_export_port}"
  to_port         = "${local.web_app_export_port}"

  # No need to setup `cidr_blocks`, as only load balancer
  # is public facing.
  security_group_id = "${aws_security_group.web_app.id}"
  source_security_group_id = "${aws_security_group.web_alb.id}"
}

resource "aws_security_group_rule" "web_app_ecs_tasks_self_ingress" {
  type            = "ingress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0

  security_group_id = "${aws_security_group.web_app.id}"
  self = true
}

resource "aws_security_group" "web_static" {
  name = "${local.web_static_resource_name}"
  vpc_id      = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "web_static_ecs_tasks_egress" {
  type            = "egress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.web_static.id}"
}

resource "aws_security_group_rule" "web_static_ecs_tasks_lb_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = "${local.web_static_export_port}"
  to_port         = "${local.web_static_export_port}"

  # No need to setup `cidr_blocks`, as only load balancer
  # is public facing.
  security_group_id = "${aws_security_group.web_static.id}"
  source_security_group_id = "${aws_security_group.web_alb.id}"
}

resource "aws_security_group_rule" "web_static_ecs_tasks_self_ingress" {
  type            = "ingress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0

  security_group_id = "${aws_security_group.web_static.id}"
  self = true
}

resource "aws_security_group" "git" {
  name = "${local.git_resource_name}"
  vpc_id      = "${aws_vpc.main.id}"

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = 22
    to_port     = 22
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "efs" {
  name = "${local.name_prefix}-efs"
  vpc_id = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "efs_egress" {
  type            = "egress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.efs.id}"
}

resource "aws_security_group_rule" "efs_ecs_tasks_ingress" {
  type            = "ingress"

  # NSF uses TCP protocol:
  # https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/security-group-rules-reference.html#sg-rules-efs
  protocol        = "tcp"
  from_port       = 2049
  to_port         = 2049

  security_group_id = "${aws_security_group.efs.id}"

  # TODO:
  # Why `aws_security_group.git` doesn't need it?
  source_security_group_id = "${aws_security_group.web_app.id}"
}

resource "aws_security_group" "postgres" {
  name = "${local.name_prefix}-postgres"
  vpc_id = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "postgres_egress" {
  type            = "egress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.postgres.id}"
}

# https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.Scenarios.html#USER_VPC.Scenario1
resource "aws_security_group_rule" "postgres_web_app_ecs_tasks_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = 5432
  to_port         = 5432

  security_group_id = "${aws_security_group.postgres.id}"
  source_security_group_id = "${aws_security_group.web_app.id}"
}

resource "aws_security_group_rule" "postgres_git_ecs_tasks_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = 5432
  to_port         = 5432

  security_group_id = "${aws_security_group.postgres.id}"
  source_security_group_id = "${aws_security_group.git.id}"
}

# TODO:
# May be able to be removed after debugging. Or replace `0.0.0.0/0` with my
# customized IP, so I can still use it to setup the initial db state.
resource "aws_security_group_rule" "postgres_psql_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = 5432
  to_port         = 5432
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.postgres.id}"
}

resource "aws_security_group" "redis" {
  name = "${local.name_prefix}-redis"
  vpc_id = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "redis_egress" {
  type            = "egress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.redis.id}"
}

# https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.Scenarios.html#USER_VPC.Scenario1
resource "aws_security_group_rule" "redis_ecs_tasks_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = 6379
  to_port         = 6379

  # Redis is for session management. Git doesn't need to access it.
  security_group_id = "${aws_security_group.redis.id}"
  source_security_group_id = "${aws_security_group.web_app.id}"
}
