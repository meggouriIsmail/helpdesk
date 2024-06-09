package com.helpdesk.ticketingmanagement.enums;

public enum TypeActivity {
    ASSIGNED_TO("ASSIGNED_TO"),
    COMMENT("COMMENT"),
    TICKET_CREATED("TICKET_CREATED"),
    STATUS_CHANGED("STATUS_CHANGED"),
    SHARED_WITH("SHARED_WITH");

    public final String value;
    TypeActivity(String value) {
        this.value = value;
    }
}
