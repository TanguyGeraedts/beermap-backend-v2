package be.tanguy.beermap.api.common.web;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String API = "/api";
    public static final String V1 = API + "/v1";

    public static final class Users {
        public static final String BASE = V1 + "/users";

        public static final String ME = "/me";

        private Users() {}
    }

    public static final class Sessions {
        public static final String BASE = V1 + "/sessions";

        private Sessions() {}
    }

    public static final class Images {
        public static final String BASE = V1 + "/images";

        public static final String PRESIGN_PROFILE_PICTURE = "/presign/profile-picture";
        public static final String PRESIGN_DRINK_LOG = "/presign/drink-log";

        private Images() {}
    }
}