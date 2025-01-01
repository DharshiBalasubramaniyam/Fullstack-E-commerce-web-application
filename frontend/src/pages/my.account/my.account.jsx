import { useContext, useEffect } from "react";
import Footer from "../../components/footer/footer";
import Header from "../../components/header/header";
import { AuthContext } from "../../contexts/auth.context";
import './my.css'
import OrderService from "../../api-service/order.service";
import Loading from "../../components/loading/loading";
import Info from "../../components/info/info";
function MyAccount() {
    const { user, toggleUser } = useContext(AuthContext)
    const { isLoading, userOrders, getOrdersByUser } = OrderService()

    return (
        <>
            <Header />
            <ProfileCard user={user} />
            {isLoading && <Loading />}
            {!isLoading && <OrderList orders={userOrders} />}
            <Footer />
        </>
    )
}

export default MyAccount;


function ProfileCard({ user }) {


    return (
        <section className="profile-card">
            <h3>Welcome back {user?.username}! <span>({user?.email})</span></h3>
        </section>
    )
}

function OrderList({ orders }) {

    return (
        <>
            {orders.length == 0 && <Info message="You have no orders placed yet!" />}

            {orders.length != 0 && (
                <>
                    <div className='orders'>
                        <h2>My orders</h2>

                        {
                            orders.map((order) => {
                                return (
                                    <div className='order' key={order.id}>
                                        <div>
                                            <div>Order #{order.id}</div>
                                            <div>Placed on {order.placedOn.split("T")[0]} {order.placedOn.split("T")[1]}</div>
                                        </div>
                                        <div className='items'>
                                            {
                                                order.orderItems.map((item) => {
                                                    return (
                                                        <div key={item.productId}>
                                                            <img src={`${item.imageUrl}`} />
                                                            <div>
                                                                <div>{item.productName}</div>
                                                                <div>Rs. {item.price} x {item.quantity}</div>
                                                            </div>
                                                        </div>
                                                    )
                                                })
                                            }
                                        </div>
                                        <div>
                                            <div>Total: Rs. {order.orderAmt}</div>
                                            <div>Order Status: {order.orderStatus}</div>
                                            <div>Paid Status: {order.paymentStatus}</div>
                                            <div>Shipped address: {order.addressLine1} {order.addressLine2}</div>
                                        </div>

                                    </div>
                                )
                            })
                        }


                    </div>
                </>
            )}
        </>
    )
}