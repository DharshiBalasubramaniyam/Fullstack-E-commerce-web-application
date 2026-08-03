import { useContext, useEffect, useState, useRef } from 'react';
import "./Assistant.css"
import { AuthContext } from '../../contexts/auth.context';
import { useNavigate } from "react-router-dom";
import { FaTimes } from 'react-icons/fa';
import { AiOutlineClose } from 'react-icons/ai';
import Markdown from "react-markdown";
import remarkBreaks from 'remark-breaks';
import AssistantContext from '../../contexts/assistant.context';
import Loading from '../loading/loading';
import remarkGfm from 'remark-gfm';

function Assistant({ isOpen, onClose }) {

    const { user } = useContext(AuthContext);
    const navigate = useNavigate()
    const { chats, queryLoading, interrupt, sessionLoading, sendQuery } = useContext(AssistantContext)
    const [query, setquery] = useState("")
    const chatContainerRef = useRef(null)

    const onSend = async () => {
        const q = query
        setquery("");
        await sendQuery(q)
    }

    const onReplyInterrupt = async (confirmed) => {
        await sendQuery(confirmed)
    }

    // Scroll to bottom when chats change or loading state changes
    useEffect(() => {
        if (chatContainerRef.current) {
            setTimeout(() => {
                chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
            }, 0);
        }
    }, [chats, queryLoading])

    return (
        <>
            <div className={isOpen ? "shoppingCart active" : "shoppingCart"}>
                <div className="header">
                    <h2>Your shopping Assistant</h2>

                    <div className="btn close-btn" onClick={onClose}>
                        <AiOutlineClose size={20} />
                    </div>
                </div>
                {sessionLoading && <Loading />}
                {
                    !sessionLoading && chats && chats.length > 0 ? (
                        <div class="chat-container" id="chat" ref={chatContainerRef}>
                            {
                                chats.map((chat) => {
                                    return (
                                        <div className={`message-wrapper ${chat.type}`}>
                                            <div
                                                className={chat.response_metadata && chat.response_metadata.interrupt && chat.type == "assistant" ? `message interrupt` : "message"}
                                            >
                                                <Markdown remarkPlugins={[remarkBreaks]}>
                                                    {chat.content}
                                                </Markdown>
                                            </div>
                                            {
                                                chat.response_metadata && chat.response_metadata.interrupt ? (
                                                    <div className='confirm-wrapper'>
                                                        <button
                                                            id="interruptN"
                                                            onClick={() => onReplyInterrupt("N")}
                                                        >No</button>
                                                        <button
                                                            id="interruptY"
                                                            onClick={() => onReplyInterrupt("Y")}
                                                        >Yes</button>
                                                    </div>
                                                ) : <></>
                                            }
                                        </div>
                                    )
                                })
                            }
                            {
                                queryLoading ? (
                                    <div className={`message-wrapper assistant`}>
                                        <div className={`message`}>
                                            <Markdown remarkPlugins={[remarkGfm, remarkBreaks]}>
                                                Loading...
                                            </Markdown>
                                        </div>
                                    </div>
                                ) : <></>
                            }
                        </div>
                    ) : <></>
                }
                <div class="input-area">
                    <textarea
                        id="userInput"
                        placeholder="Type your question..."
                        value={query}
                        disabled={queryLoading || interrupt}
                        onChange={(e) => setquery(e.target.value)}
                    />
                    <button
                        id="sendBtn"
                        onClick={onSend}
                        disabled={queryLoading || interrupt}
                    ><i className='fa fa-send'></i></button>
                </div>
            </div>

        </>
    )
}

export default Assistant;