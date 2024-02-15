import {
    Alert,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    InputAdornment,
    Stack,
    TextField
} from "@mui/material";
import {t} from "i18next";
import {deleteRequest, patchRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";

export default ({open, setOpen, uuid, setAlert}) => {

    const {updateServer} = useContext(ServerContext);

    const deleteServer = async () => {
        setOpen(false);
        await deleteRequest("server/", {uuid});
        updateServer();
        setAlert({severity: "success", message: t("server.dialog.delete.success")});
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("server.dialog.delete.title")}</DialogTitle>
            <DialogContent>
                <Alert severity="warning" sx={{width: "20rem"}}>{t("server.dialog.delete.warning")}</Alert>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("common.cancel")}</Button>
                <Button onClick={deleteServer} color="error">{t("server.dialog.delete.button")}</Button>
            </DialogActions>
        </Dialog>
    );
}