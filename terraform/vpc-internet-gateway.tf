resource "aws_internet_gateway" "purely_igw" {
  vpc_id = aws_vpc.purely_vpc.id

  tags = {
    Name = "purely_igw"
  }
}