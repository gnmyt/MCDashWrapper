import {Button, Stack, Typography} from "@mui/material";
import {t} from "i18next";
import {AvTimer, Bolt, Group, PowerOff, Share} from "@mui/icons-material";
import {useEffect} from "react";
import {proxyRequest} from "@/common/utils/RequestUtil.js";

export const ServerInfo = ({status, uuid, serverInfo, setServerInfo, setAlert}) => {

    const updateServerInfo = () => {
        proxyRequest(uuid + "/api/stats/")
            .then((response) => {
                if (response.status === 200) return response.json();
                throw new Error("Server is offline");
            })
            .then((data) => setServerInfo(data))
            .catch(() => setServerInfo(null));
    }

    useEffect(() => {
        updateServerInfo();

        const interval = setInterval(() => updateServerInfo(), 5000);

        return () => clearInterval(interval);
    }, []);


    return (
        <Stack direction="row" gap={2} alignItems="center">
            {status === "ONLINE" && serverInfo === null && (
                <Button variant="outlined" color="error" onClick={() => setAlert({severity: "info", message: t("server.waiting")})}
                        startIcon={<PowerOff/>}>{t("server.not_connected")}
                </Button>)}
            {status === "ONLINE" && serverInfo !== null && Object.keys(serverInfo).length === 0 && (
                <Button variant="outlined" color="warning" onClick={() => setAlert({severity: "info", message: t("server.loading")})}
                        startIcon={<Bolt/>}>{t("server.loading")}
                </Button>)}
            {status === "ONLINE" && serverInfo !== null && Object.keys(serverInfo).length > 0 && (
                <Stack direction="row" gap={3} alignItems="center"
                       sx={{display: {xs: "none", md: "none", lg: "flex"}}}>
                    <Stack direction="row" gap={1} alignItems="center">
                        <Group/>
                        <Typography
                            variant="body2">{serverInfo.online_players} / {serverInfo.max_players}</Typography>
                    </Stack>
                    <Stack direction="row" gap={1} alignItems="center">
                        <AvTimer/>
                        <Typography variant="body2">{serverInfo.tps}</Typography>
                    </Stack>
                    <Stack direction="row" gap={1} alignItems="center">
                        <Button variant="outlined" color="info" onClick={() => setAlert({severity: "info", message: t("coming_soon")})}
                                startIcon={<Share/>}>{t("server.share")}
                        </Button>
                    </Stack>
                </Stack>
            )}
        </Stack>
    )
}