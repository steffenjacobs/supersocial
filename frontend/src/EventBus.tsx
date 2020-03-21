export enum EventBusEventType {SELECTED_POST_CHANGED, REFRESH_POSTS, USER_CHANGE};

export class EventBus {
    handlers: Map<EventBusEventType, Set<(eventType: EventBusEventType, eventData?:any) => void>>;
    constructor() {
        this.handlers = new Map<EventBusEventType, Set<(eventType: EventBusEventType) => void>>();
    }

    public async fireEvent(eventType: EventBusEventType, eventData?: any){
        this.handlers.forEach((value: Set<(eventType: EventBusEventType, eventData?:any) => void>, key: EventBusEventType)=>{
            if(key !== eventType){
                return;
            }
            value.forEach((value: (eventType: EventBusEventType, eventData?:any) => void) => {
                value(eventType, eventData);
            });
        });
    }

    public register(eventType: EventBusEventType, handler: (eventType: EventBusEventType, eventData?:any) => void) {
        var handlers = this.handlers.get(eventType);
        if (!handlers) {
            handlers = new Set<(eventType: EventBusEventType, eventData?:any) => void>();
        }
        handlers.add(handler);
        this.handlers.set(eventType, handlers);
    }

    public unregister(eventType: EventBusEventType, handler: (eventType: EventBusEventType, eventData?:any) => void) {
        var handlers = this.handlers.get(eventType);
        if (!handlers) {
            throw new ReferenceError("Handler was never registered");
        }
        handlers.delete(handler);
        if (handlers.size === 0) {
            this.handlers.delete(eventType);
        } else {

            this.handlers.set(eventType, handlers);
        }
    }
}