export enum EventBusEventType { SELECTED_POST_CHANGED, REFRESH_POSTS, USER_CHANGE, REFRESH_SOCIAL_MEDIA_ACCOUNTS, SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, REFRESH_POST_ANALYTICS, REFRESH_ACCOUNT_ANALYTICS, REFRESH_POST_ANALYTICS_REQ, REFRESH_ACCOUNT_ANALYTICS_REQ, SELECTED_TEAM_CHANGED, REFRESH_TEAMS }

/** Event Bus where listeners can be registered and events can be fired to avoid endless callbacks. */
export class EventBus {
    handlers: Map<EventBusEventType, Set<(eventType: EventBusEventType, eventData?: any) => void>>;
    constructor() {
        this.handlers = new Map<EventBusEventType, Set<(eventType: EventBusEventType) => void>>();
    }

    /** Fires an event asynchronously. The registered handlers will be triggered eventually. */
    public async fireEvent(eventType: EventBusEventType, eventData?: any) {
        this.handlers.forEach((value: Set<(eventType: EventBusEventType, eventData?: any) => void>, key: EventBusEventType) => {
            if (key !== eventType) {
                return;
            }
            value.forEach((handler: (eventType: EventBusEventType, eventData?: any) => void) => {
                handler(eventType, eventData);
            });
        });
    }

    /** Registers a new event handler for the given eventType. It will be called when an event of this type is fired. */
    public register(eventType: EventBusEventType, handler: (eventType: EventBusEventType, eventData?: any) => void) {
        var handlers = this.handlers.get(eventType);
        if (!handlers) {
            handlers = new Set<(eventType: EventBusEventType, eventData?: any) => void>();
        }
        handlers.add(handler);
        this.handlers.set(eventType, handlers);
    }

    /** Unregisters an existing event handler for the given eventType. */
    public unregister(eventType: EventBusEventType, handler: (eventType: EventBusEventType, eventData?: any) => void) {
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