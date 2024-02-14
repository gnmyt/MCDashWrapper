import {createContext, useEffect, useState} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil.js";

export const ServerContext = createContext({});

export const ServerProvider = ({children}) => {

    const [server, setServer] = useState(null);

    const updateServer = () => {
        jsonRequest("server/").then((data) => setServer(data));
    }

    useEffect(() => {
        updateServer();

        const interval = setInterval(updateServer, 5000);

        return () => clearInterval(interval);
    }, []);

    return (
        <ServerContext.Provider value={{server, updateServer}}>
            {children}
        </ServerContext.Provider>
    )
}