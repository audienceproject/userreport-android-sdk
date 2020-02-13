package com.audienceproject.userreport;

/**
 * This entity of this interface returned by AudienceBuilder and user for in app event tracking.
  */

public interface IInAppEventsTrack {
    /**
     * Important to call this method when context destroys
     */
    void destroy();
}
