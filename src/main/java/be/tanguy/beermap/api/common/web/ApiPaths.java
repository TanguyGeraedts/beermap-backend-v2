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

    // Future:
    public static final class Sessions {
        public static final String BASE = V1 + "/sessions";

        private Sessions() {}
    }
}