resource "aws_eip" "purely_nat_gateway_eip" {
    domain = "vpc"
    tags = {
        Name = "purely_nat_gateway_eip"
    }
}

resource "aws_nat_gateway" "purely_nat_gateway" {
    allocation_id = aws_eip.purely_nat_gateway_eip.id
    subnet_id = aws_subnet.purely_public_subnet_1a.id 

    tags = {
        Name = "purely_nat_gateway"
    }

    depends_on = [
        aws_eip.purely_nat_gateway_eip
    ]
}