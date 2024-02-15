import {
    Box,
    Button,
    CircularProgress,
    Stack,
    Typography
} from "@mui/material";
import {ServerContext} from "@/common/contexts/Server";
import {useContext, useState} from "react";
import Server from "@/states/Root/pages/Overview/components/Server/index.js";
import {CreationDialog} from "@/states/Root/pages/Overview/components/CreationDialog/CreationDialog.jsx";
import {t} from "i18next";

export const Overview = () => {

    const [dialogOpen, setDialogOpen] = useState(false);
    const {server} = useContext(ServerContext);

    return (
        <>
            <Stack direction="column" gap={2}>

                <Stack direction="row" gap={2} alignItems="center" justifyContent="space-between">
                    <Typography fontWeight={500} variant="h5">{t("server.overview")}</Typography>
                    <Button variant="outlined" color="primary" onClick={() => setDialogOpen(true)}>{t("server.create")}</Button>
                </Stack>

                {server === null && (
                    <Box alignItems="center" justifyContent="center" display="flex">
                        <CircularProgress/>
                    </Box>
                )}

                {dialogOpen && <CreationDialog dialogOpen={dialogOpen} setDialogOpen={setDialogOpen}/>}

                {server !== null && server.map((server) => (
                    <Server key={server.uuid} uuid={server.uuid} configuration={server.configuration}
                            status={server.status}/>
                ))}
            </Stack>
        </>
    )
}