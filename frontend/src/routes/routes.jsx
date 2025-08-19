import { Suspense, lazy, useContext, useMemo } from 'react';
import { Routes, Route, Navigate, Outlet } from "react-router-dom";
import Loading from "../components/loading/loading";
import Products from '../pages/products/products.jsx';
import CheckoutForm from '../pages/checkout/checkout.jsx';
import Search from '../pages/search/search.jsx';
import OrderSuccess from '../pages/checkout/order.success.jsx';
import MyAccount from '../pages/my.account/my.account.jsx';
import NotFound from '../pages/auth/auth_error/notfound.jsx';
import Unauthorized from '../pages/auth/auth_error/unauthorized.jsx';
import { AuthContext } from '../contexts/auth.context.jsx';

const Home = lazy(() => import('../pages/home/home.jsx'))
const Login = lazy(() => import('../pages/auth/login/login.jsx'))
const Register = lazy(() => import('../pages/auth/register/register.jsx'))
const RegistrationVerfication = lazy(() => import('../pages/auth/register/registration.verification.jsx'))
const RegistrationSuccessful = lazy(() => import('../pages/auth/register/registration.success.jsx'))

function AppRoutes() {
  return (
    <Suspense fallback={<Loading />}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products/:category" element={<Products />} />
        <Route path="/search/:search" element={<Search />} />
        <Route path="*" element={<NotFound />} />
        <Route path="/unauthorized" element={<Unauthorized />} />
        <Route path="/auth/login" element={<Login />} />
        <Route path="/auth/register" element={<Register />} />
        <Route path="/auth/userRegistrationVerfication/:email" element={<RegistrationVerfication />} />
        <Route path="/auth/success-registration" element={<RegistrationSuccessful />} />
        <Route path="/my/account" element={<MyAccount />} />
        <Route path="/order/checkout" element={<CheckoutForm />} />
        <Route path="/order/success" element={<OrderSuccess />} />  
      </Routes>
    </Suspense>
  )
}

export default AppRoutes;