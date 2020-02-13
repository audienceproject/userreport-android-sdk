package com.audienceproject.userreport.models;

/**
 * For know customization contains only one field.
 */
public class Customization {
    /**
     * If true than standard close button of survey will not be drawn
     * <p>
     * In this case customer will need to draw own close button using standard Android UI components.
     */
    public boolean hideCloseButton;

    public boolean isCustomTab;
}
