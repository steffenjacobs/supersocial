import { toast } from "react-toastify";

export interface InterfaceWithError {
    error: string
}

/**Shows different kinds of standardized toasts. */
export class ToastManager {
    static showErrorToast(response: Response) {
        response.json().then(json => {
            toast.error(json.error, {
                position: toast.POSITION.TOP_RIGHT
            });
        });
    }
    static showSuccessToast(message: string) {
        toast.success(message, {
            position: toast.POSITION.TOP_RIGHT
        });
    }
    static showWarnToast(message: string) {
        toast.warn(message, {
            position: toast.POSITION.TOP_RIGHT
        });
    }
    static showInfoToast(message: string) { 
        toast.info(message, {
            position: toast.POSITION.TOP_RIGHT
        });
    }
}