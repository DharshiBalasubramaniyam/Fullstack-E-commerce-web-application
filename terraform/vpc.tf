resource "aws_vpc" "purely_vpc" {
	cidr_block = var.vpc_cidr

	tags = {
		Name = "purely_vpc"
	}
}