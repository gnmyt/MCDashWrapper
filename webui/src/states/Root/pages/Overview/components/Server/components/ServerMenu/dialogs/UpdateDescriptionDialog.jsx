import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField} from "@mui/material";
import {t} from "i18next";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";

export default ({open, setOpen, uuid, setAlert, current}) => {

    const {updateServer} = useContext(ServerContext);
    const [description, setDescription] = useState(current || "");

    const updateDescription = async () => {
        if (!description) return;

        setOpen(false);
        await patchRequest("server/", {uuid, description});
        updateServer();
        setAlert({severity: "success", message: t("server.dialog.update_description.success")});
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("server.dialog.update_description.title")}</DialogTitle>
            <DialogContent>
                <Stack marginTop={1} maxWidth="20rem">
                    <TextField label={t("server.creation.description")} variant="outlined" multiline rows={2} fullWidth
                               value={description} onChange={(e) => setDescription(e.target.value)}/>
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("common.cancel")}</Button>
                <Button onClick={updateDescription} color="primary">{t("server.dialog.update_description.button")}</Button>
            </DialogActions>
        </Dialog>
    );
}