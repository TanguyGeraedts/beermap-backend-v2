package be.tanguy.beermap.api.common.web;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String API = "/api";
    public static final String V1 = API + "/v1";

    public static final class Meta {
        public static final String BASE = V1 + "/meta";
        public static final String CHANGELOG = "/changelog";
        private Meta() {}
    }

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
        public static final String DELETE = "/delete";

        private Images() {}
    }

    public static final class Catalog {
        public static final String BASE = V1 + "/catalog";

        public static final class Drinks {
            public static final String BASE = Catalog.BASE + "/drinks";

            public static final String BY_NAME = "/by-name";
            public static final String BY_CATEGORY = "/by-category";

            public static final String SEARCH_NAME = "/search/name";
            public static final String SEARCH_SUB_TYPE = "/search/sub-type";
            public static final String SEARCH_CATEGORY_NAME = "/search/category-name";

            private Drinks() {}
        }

        private Catalog() {}
    }

    public static final class Friendships {
        public static final String BASE = V1 + "/friendships";

        public static final String REQUEST = "/request";
        public static final String ACCEPT = "/accept";
        public static final String REJECT = "/reject";
        public static final String CANCEL = "/cancel";
        public static final String REMOVE = "/remove";
        public static final String BLOCK = "/block";

        public static final String RESEND = "/resend";
        public static final String UNBLOCK = "/unblock";

        public static final String INCOMING = "/incoming";
        public static final String OUTGOING = "/outgoing";
        public static final String FRIENDS = "/friends";
        public static final String BLOCKED = "/blocked";

        public static final String BY_USER = "/{userId}";

        private Friendships() {}
    }

    public static final class DrinkLog{
        public static final String BASE = V1 + "/drink-logs";

        public static final String ME = "/me";
        public static final String ID = "/{logId}";

    }

    public static final class DraftLog{
        public static final String BASE = V1 + "/draft-logs";

        public static final String ME = "/me";
        public static final String ID = "/{draftLogId}";
    }

    public static final class Stats {
        public static final String BASE = V1 + "/stats";

        public static final String ME = "/me";

        private Stats() {}
    }

    public static final class Feed {
        public static final String BASE = V1 + "/feed";
        public static final String ID = "/{feedItemId}";
        public static final String CHEERS = ID + "/cheers";

        private Feed() {}
    }
}
