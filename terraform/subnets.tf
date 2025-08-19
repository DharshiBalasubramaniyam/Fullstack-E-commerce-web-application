resource "aws_subnet" "purely_public_subnet_1a" {
	vpc_id     = aws_vpc.purely_vpc.id
	cidr_block = "10.0.2.0/23"
	availability_zone = "us-east-1a"
	map_public_ip_on_launch = true

	tags = {
		Name = "purely_public_subnet_1a",
		"kubernetes.io/role/elb" = 1
	}

	depends_on = [
		aws_vpc.purely_vpc
	]
}

resource "aws_subnet" "purely_public_subnet_1b" {
	vpc_id     = aws_vpc.purely_vpc.id
	cidr_block = "10.0.4.0/23"
	availability_zone = "us-east-1b"
	map_public_ip_on_launch = true

	tags = {
		Name = "purely_public_subnet-1b",
		"kubernetes.io/role/elb" = 1
	}

	depends_on = [
		aws_vpc.purely_vpc
	]
}

resource "aws_subnet" "purely_private_subnet_1a" {
	vpc_id     = aws_vpc.purely_vpc.id
	cidr_block = "10.0.6.0/23"
	availability_zone = "us-east-1a"

	tags = {
		Name = "purely_private_subnet_1a",
		"kubernetes.io/role/internal-elb" = 1
	}

	depends_on = [
		aws_vpc.purely_vpc
	]
}

resource "aws_subnet" "purely_private_subnet_1b" {
	vpc_id     = aws_vpc.purely_vpc.id
	cidr_block = "10.0.8.0/23"
	availability_zone = "us-east-1b"

	tags = {
		Name = "purely_private_subnet_1b",
		"kubernetes.io/role/internal-elb" = 1
	}

	depends_on = [
		aws_vpc.purely_vpc
	]
}