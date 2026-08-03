import { useState, useEffect } from "react"
import API_BASE_URL from "./apiConfig";
import axios from 'axios';

function AssistantService() {
    const [chats, setchats] = useState([])
    const [sessionLoading, setsessionLoading] = useState(false)
    const [error, setError] = useState(false)
    const [interrupt, setinterrupt] = useState(false)
    const [queryLoading, setqueryLoading] = useState(false)
    const user = JSON.parse(localStorage.getItem("user"));

    console.log(interrupt)

    const authHeader = () => {
        return { Authorization: `${user?.type}${user?.token}` };
    }

    const startSession = async () => {
        console.log("session start")
        setsessionLoading(true)
        await axios.get(
            `${API_BASE_URL}/ai-service/session`,
            { headers: authHeader(), withCredentials: true }
        )
            .then((response) => {
                console.log("s ", response.data.thread_id, response.data.messages)
                const _chats = []
                response.data.messages.map(chat => {
                    _chats.push({type: chat.type == "ai" ? "assistant" : "human", content: chat.content.replaceAll("\\n", "\n"), response_metadata: chat.response_metadata})
                }) 
                setchats(_chats)
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        console.log("session end.")
        setsessionLoading(false)
    }

    const sendQuery = async (query) => {
        setqueryLoading(true)
        let existChats1 = chats
        existChats1.push({
            type: "human", content: query
        })
        setchats(existChats1)
        await axios.post(
            `${API_BASE_URL}/ai-service/chat`,
            {query: query},
            { headers: authHeader(), withCredentials: true }
        )
            .then((response) => {
                console.log("q ", response.data, response.data?.response_metadata && response?.data?.response_metadata.interrupt)
                let existChats2 = chats
                existChats2.push(response.data)
                setchats(existChats2)
                if (response.data?.response_metadata && response.data?.response_metadata?.interrupt) {
                    setinterrupt(true);
                } else {
                    setinterrupt(false)
                }
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        setqueryLoading(false)
    }

     useEffect(() => {
        startSession()
     }, [])

    return { sendQuery, chats, interrupt, queryLoading, sessionLoading };

}

export default AssistantService;