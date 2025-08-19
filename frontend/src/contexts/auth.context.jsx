import { createContext, useEffect, useState } from "react";

export const AuthContext = createContext();

export const useAuth = () => {
    const [user, setUser] = useState()

    const toggleUser = () => {
        const newUser = JSON.parse(localStorage.getItem("user"));
        if (JSON.stringify(user) != JSON.stringify(newUser)) {
            setUser(newUser)
        }
    };

    useEffect(() => {
        toggleUser()
        console.log("toggling user")
    }, [])

    return { user, toggleUser }
}