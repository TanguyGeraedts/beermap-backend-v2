package be.tanguy.beermap.api.map.repository;

import be.tanguy.beermap.api.logs.domain.DrinkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MapRepository extends JpaRepository<DrinkLog, UUID> {

    /// Private / Friends
    @Query(value = """
            SELECT
              dl.id                                        AS logId,
              dl.latitude                                  AS latitude,
              dl.longitude                                 AS longitude,
              dl.category                                  AS category,
              COALESCE(dl.catalog_item_name,
                       dl.custom_drink_name)               AS displayName,
              dl.sub_type                                  AS subType,
              up.id                                        AS userId,
              up.username                                  AS username,
              up.discriminator                             AS discriminator,
              up.avatar_url                                AS avatarUrl,
              up.account_type                              AS accountType,
              dl.rating                                    AS rating,
              dl.photo_url                                 AS photoUrl,
              dl.consumed_at                               AS consumedAt
            FROM drink_log dl
            JOIN user_profiles up ON up.id = dl.user_id
            WHERE dl.latitude  IS NOT NULL
              AND dl.longitude IS NOT NULL
              AND dl.latitude  BETWEEN :minLat AND :maxLat
              AND dl.longitude BETWEEN :minLon AND :maxLon
              AND dl.user_id IN :userIds
              AND (:from IS NULL OR dl.consumed_at >= :from)
              AND (:to   IS NULL OR dl.consumed_at <= :to)
            ORDER BY dl.consumed_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<MapPinProjection> findMapPinsForUsers(
            @Param("userIds") List<UUID>   userIds,
            @Param("minLat")  double       minLat,
            @Param("minLon")  double       minLon,
            @Param("maxLat")  double       maxLat,
            @Param("maxLon")  double       maxLon,
            @Param("from")    Instant      from,
            @Param("to")      Instant      to,
            @Param("limit")   int          limit
    );

    @Query(value = """
            SELECT
              dl.id                                        AS logId,
              dl.latitude                                  AS latitude,
              dl.longitude                                 AS longitude,
              dl.category                                  AS category,
              COALESCE(dl.catalog_item_name,
                       dl.custom_drink_name)               AS displayName,
              dl.sub_type                                  AS subType,
              up.id                                        AS userId,
              up.username                                  AS username,
              up.discriminator                             AS discriminator,
              up.avatar_url                                AS avatarUrl,
              up.account_type                              AS accountType,
              dl.rating                                    AS rating,
              dl.photo_url                                 AS photoUrl,
              dl.consumed_at                               AS consumedAt
            FROM drink_log dl
            JOIN user_profiles up ON up.id = dl.user_id
            WHERE dl.latitude  IS NOT NULL
              AND dl.longitude IS NOT NULL
              AND dl.latitude  BETWEEN :minLat AND :maxLat
              AND dl.longitude BETWEEN :minLon AND :maxLon
              AND dl.user_id IN :userIds
              AND dl.category IN :categories
              AND (:from IS NULL OR dl.consumed_at >= :from)
              AND (:to   IS NULL OR dl.consumed_at <= :to)
            ORDER BY dl.consumed_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<MapPinProjection> findMapPinsForUsersFiltered(
            @Param("userIds")    List<UUID>   userIds,
            @Param("minLat")     double       minLat,
            @Param("minLon")     double       minLon,
            @Param("maxLat")     double       maxLat,
            @Param("maxLon")     double       maxLon,
            @Param("categories") List<String> categories,
            @Param("from")       Instant      from,
            @Param("to")         Instant      to,
            @Param("limit")      int          limit
    );

    /// Global
    @Query(value = """
            SELECT
              dl.id                                        AS logId,
              dl.latitude                                  AS latitude,
              dl.longitude                                 AS longitude,
              dl.category                                  AS category,
              COALESCE(dl.catalog_item_name,
                       dl.custom_drink_name)               AS displayName,
              dl.sub_type                                  AS subType,
              up.id                                        AS userId,
              up.username                                  AS username,
              up.discriminator                             AS discriminator,
              up.avatar_url                                AS avatarUrl,
              up.account_type                              AS accountType,
              dl.rating                                    AS rating,
              dl.photo_url                                 AS photoUrl,
              dl.consumed_at                               AS consumedAt
            FROM drink_log dl
            JOIN user_profiles up ON up.id = dl.user_id
            WHERE dl.latitude  IS NOT NULL
              AND dl.longitude IS NOT NULL
              AND dl.latitude  BETWEEN :minLat AND :maxLat
              AND dl.longitude BETWEEN :minLon AND :maxLon
              AND (:from IS NULL OR dl.consumed_at >= :from)
              AND (:to   IS NULL OR dl.consumed_at <= :to)
            ORDER BY dl.consumed_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<MapPinProjection> findMapPinsForAll(
            @Param("minLat")  double  minLat,
            @Param("minLon")  double  minLon,
            @Param("maxLat")  double  maxLat,
            @Param("maxLon")  double  maxLon,
            @Param("from")    Instant from,
            @Param("to")      Instant to,
            @Param("limit")   int     limit
    );

    @Query(value = """
            SELECT
              dl.id                                        AS logId,
              dl.latitude                                  AS latitude,
              dl.longitude                                 AS longitude,
              dl.category                                  AS category,
              COALESCE(dl.catalog_item_name,
                       dl.custom_drink_name)               AS displayName,
              dl.sub_type                                  AS subType,
              up.id                                        AS userId,
              up.username                                  AS username,
              up.discriminator                             AS discriminator,
              up.avatar_url                                AS avatarUrl,
              up.account_type                              AS accountType,
              dl.rating                                    AS rating,
              dl.photo_url                                 AS photoUrl,
              dl.consumed_at                               AS consumedAt
            FROM drink_log dl
            JOIN user_profiles up ON up.id = dl.user_id
            WHERE dl.latitude  IS NOT NULL
              AND dl.longitude IS NOT NULL
              AND dl.latitude  BETWEEN :minLat AND :maxLat
              AND dl.longitude BETWEEN :minLon AND :maxLon
              AND dl.category IN :categories
              AND (:from IS NULL OR dl.consumed_at >= :from)
              AND (:to   IS NULL OR dl.consumed_at <= :to)
            ORDER BY dl.consumed_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<MapPinProjection> findMapPinsForAllFiltered(
            @Param("minLat")     double       minLat,
            @Param("minLon")     double       minLon,
            @Param("maxLat")     double       maxLat,
            @Param("maxLon")     double       maxLon,
            @Param("categories") List<String> categories,
            @Param("from")       Instant      from,
            @Param("to")         Instant      to,
            @Param("limit")      int          limit
    );
}