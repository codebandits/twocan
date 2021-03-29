import {useCallback, useState} from "react";

type DialogState = {
    open: boolean
    handleOpen: () => void
    handleClose: () => void
}

type DialogStateOptions = {
    onOpenCallback?: () => void
    onCloseCallback?: () => void
}

export const useDialogState = ({onOpenCallback, onCloseCallback}: DialogStateOptions = {}): DialogState => {
    const [open, setOpen] = useState(false)
    const handleOpen = useCallback(() => {
        setOpen(true)
        onOpenCallback?.()
    }, [onOpenCallback])
    const handleClose = useCallback(() => {
        setOpen(false)
        onCloseCallback?.()
    }, [onCloseCallback])
    return {open, handleOpen, handleClose}
}
