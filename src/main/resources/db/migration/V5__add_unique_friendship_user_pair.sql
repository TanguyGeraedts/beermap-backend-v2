DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM friendships
        GROUP BY
            LEAST(requester_id, addressee_id),
            GREATEST(requester_id, addressee_id)
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Cannot add unordered friendship uniqueness while duplicate friendship pairs exist';
    END IF;
END $$;

CREATE UNIQUE INDEX ux_friendships_user_pair
    ON friendships (
        LEAST(requester_id, addressee_id),
        GREATEST(requester_id, addressee_id)
    );
