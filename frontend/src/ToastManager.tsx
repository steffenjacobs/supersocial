import { toast } from "react-toastify";

export interface InterfaceWithError {
    error: string
}

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
}