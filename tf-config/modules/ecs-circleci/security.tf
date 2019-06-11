# ALB Security Group: Edit this to restrict access to the application
resource "aws_security_group" "lb" {
  name = "${local.aws_alb_security_group}"
  vpc_id      = "${aws_vpc.main.id}"

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = "${var.http_port}"
    to_port     = "${var.http_port}"
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
  name = "${local.aws_web_app_security_group}"
  vpc_id      = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "ecs_tasks_egress" {
  type            = "egress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0
  cidr_blocks     = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.web_app.id}"
}

resource "aws_security_group_rule" "ecs_tasks_lb_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = "${var.tomcat_container_port}"
  to_port         = "${var.tomcat_container_port}"

  # No need to setup `cidr_blocks`, as only load balancer
  # is public facing.
  security_group_id = "${aws_security_group.web_app.id}"
  source_security_group_id = "${aws_security_group.lb.id}"
}

resource "aws_security_group_rule" "ecs_tasks_self_ingress" {
  type            = "ingress"

  protocol        = "-1"
  from_port       = 0
  to_port         = 0

  security_group_id = "${aws_security_group.web_app.id}"
  self = true
}

# TODO:
# May be able to be removed after debugging
resource "aws_security_group_rule" "ecs_tasks_ssh_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = 22
  to_port         = 22

  # TODO: `Custom IP` rather than `Anywhere`
  cidr_blocks = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.web_app.id}"
}

resource "aws_security_group" "efs" {
  name = "${local.aws_efs_security_group}"
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
  source_security_group_id = "${aws_security_group.web_app.id}"
}

resource "aws_security_group" "postgres" {
  name = "${local.aws_postgres_security_group}"
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
resource "aws_security_group_rule" "postgres_ecs_tasks_ingress" {
  type            = "ingress"

  protocol        = "tcp"
  from_port       = 5432
  to_port         = 5432

  security_group_id = "${aws_security_group.postgres.id}"
  source_security_group_id = "${aws_security_group.web_app.id}"
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
  name = "${local.aws_redis_security_group}"
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

  security_group_id = "${aws_security_group.redis.id}"
  source_security_group_id = "${aws_security_group.web_app.id}"
}