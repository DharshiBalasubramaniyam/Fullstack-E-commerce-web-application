resource "aws_route_table" "purely_public_rt" {
    vpc_id = aws_vpc.purely_vpc.id

    route {
        cidr_block = var.vpc_cidr
        gateway_id = "local"
    }

    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.purely_igw.id
    }

    tags = {
        Name = "purely_public_rt"
    }
}

resource "aws_route_table" "purely_private_rt" {
    vpc_id = aws_vpc.purely_vpc.id

    route {
        cidr_block = var.vpc_cidr
        gateway_id = "local"
    }

    route {
        cidr_block = "0.0.0.0/0"
        nat_gateway_id = aws_nat_gateway.purely_nat_gateway.id
    }

    tags = {
        Name = "purely_private_rt"
    }
}

resource "aws_route_table_association" "purely_public_subnet_1a_rt" {
  subnet_id      = aws_subnet.purely_public_subnet_1a.id
  route_table_id = aws_route_table.purely_public_rt.id

  depends_on = [
    aws_subnet.purely_public_subnet_1a,
    aws_route_table.purely_public_rt
  ]
}

resource "aws_route_table_association" "purely_public_subnet_1b_rt" {
  subnet_id      = aws_subnet.purely_public_subnet_1b.id
  route_table_id = aws_route_table.purely_public_rt.id

  depends_on = [
    aws_subnet.purely_public_subnet_1b,
    aws_route_table.purely_public_rt
  ]
}

resource "aws_route_table_association" "purely_private_subnet_1a_rt" {
  subnet_id      = aws_subnet.purely_private_subnet_1a.id
  route_table_id = aws_route_table.purely_private_rt.id

  depends_on = [
    aws_subnet.purely_private_subnet_1a,
    aws_route_table.purely_private_rt
  ]
}

resource "aws_route_table_association" "purely_private_subnet_1b_rt" {
  subnet_id      = aws_subnet.purely_private_subnet_1b.id
  route_table_id = aws_route_table.purely_private_rt.id

  depends_on = [
    aws_subnet.purely_private_subnet_1b,
    aws_route_table.purely_private_rt
  ]
}