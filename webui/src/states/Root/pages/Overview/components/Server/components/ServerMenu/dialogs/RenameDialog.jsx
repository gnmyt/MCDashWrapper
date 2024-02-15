import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField} from "@mui/material";
import {t} from "i18next";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";

export default ({open, setOpen, uuid, setAlert, current}) => {

    const {updateServer} = useContext(ServerContext);
    const [name, setName] = useState(current || "");

    const renameServer = async () => {
        if (name === "") {
            setAlert({severity: "error", message: t("server.creation.error.name")});
            return;
        }

        setOpen(false);
        await patchRequest("server/", {uuid, name});
        updateServer();
        setAlert({severity: "success", message: t("server.dialog.rename.success")});
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("server.dialog.rename.title")}</DialogTitle>
            <DialogContent>
                <Stack marginTop={1} maxWidth="12rem">
                    <TextField label={t("server.creation.name")} variant="outlined" fullWidth value={name}
                               onChange={(e) => setName(e.target.value)}/>
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("common.cancel")}</Button>
                <Button onClick={renameServer} color="primary">{t("server.dialog.rename.button")}</Button>
            </DialogActions>
        </Dialog>
    );
}