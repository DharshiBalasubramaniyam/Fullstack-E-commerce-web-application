import { Suspense, lazy } from 'react';
import { Routes, Route } from "react-router-dom";
import Loading from "../components/loading/loading";
import Products from '../pages/products/products.jsx';

const Home = lazy(() => import('../pages/home/home.jsx'))
const Login = lazy(() => import('../pages/auth/login/login.jsx'))
const Register = lazy(() => import('../pages/auth/register/register.jsx'))
const RegistrationVerfication = lazy(() => import('../pages/auth/register/registration.verification.jsx'))
const RegistrationSuccessful = lazy(() => import('../pages/auth/register/registration.success.jsx'))

function AppRoutes() {

    const ProtectedRoute = ({ isAllowed, redirectPath = '/unauthorized', children }) => {
        if (!isAllowed) {
            return <Navigate to={redirectPath} replace />;
        }

        return children ? children : <Outlet />
    }


    return (
        <Suspense fallback={<Loading />}>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/products/:category" element={<Products />} />
                <Route path="/auth/login" element={<Login />} />
                <Route path="/auth/register" element={<Register />} />
                <Route path="/auth/userRegistrationVerfication/:email" element={<RegistrationVerfication />} />
                <Route path="/auth/success-registration" element={<RegistrationSuccessful />} />
            </Routes>
        </Suspense>
    )
}

export default AppRoutes;