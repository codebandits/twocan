import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@material-ui/core";
import React from "react";
import {useFormData} from "../form";
import {useAddBird} from "./add-bird";

type Props = {
    open: boolean
    handleClose: () => void
}

const initialValues = {
    firstName: '',
    lastName: '',
}

export const AddBirdDialog = ({open, handleClose}: Props) => {
    const addBird = useAddBird()
    const formData = useFormData({
        initialValues: initialValues,
        submit: addBird,
        onSuccessCallback: handleClose,
    })
    return (
        <Dialog open={open} onClose={handleClose}>
            <form onSubmit={formData.onSubmit}>
                <DialogTitle>Add Bird</DialogTitle>
                <DialogContent>
                    <TextField
                        {...formData.fields.firstName.textFieldProps}
                        autoFocus
                        margin="dense"
                        id="add-bird-first-name-input"
                        label="First Name"
                        fullWidth/>
                    <TextField
                        {...formData.fields.lastName.textFieldProps}
                        margin="dense"
                        id="add-bird-last-name-input"
                        label="Last Name"
                        fullWidth/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button
                        {...formData.submitButtonProps}
                        color="primary">
                        Add
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    )
}
