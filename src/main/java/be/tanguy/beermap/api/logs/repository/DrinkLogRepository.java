package be.tanguy.beermap.api.logs.repository;

import be.tanguy.beermap.api.logs.domain.DrinkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DrinkLogRepository extends JpaRepository<DrinkLog, UUID> {

    Page<DrinkLog> findByUserIdOrderByConsumptionConsumedAtDesc(UUID userId, Pageable pageable);

    List<DrinkLog> findTop20ByUserIdOrderByConsumptionConsumedAtDesc(UUID userId);

    List<DrinkLog> findByUserIdAndConsumptionConsumedAtBetweenOrderByConsumptionConsumedAtAsc(
            UUID userId,
            Instant fromInclusive,
            Instant toExclusive
    );

    long countByUserIdAndConsumptionConsumedAtBetween(
            UUID userId,
            Instant fromInclusive,
            Instant toExclusive
    );

    long deleteByUserId(UUID userId);

    long countByUserId(UUID userId);

    @Query(value = """
            SELECT COALESCE(SUM(l.quantity), 0)
            FROM drink_log l
            WHERE l.user_id = :userId
            """, nativeQuery = true)
    long sumQuantityByUserId(@Param("userId") UUID userId);

    @Query(value = """
            SELECT COUNT(DISTINCT (l.consumed_at AT TIME ZONE 'UTC')::date)
            FROM drink_log l
            WHERE l.user_id = :userId
            """, nativeQuery = true)
    long countActiveDays(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
              ((l.consumed_at AT TIME ZONE 'UTC')::date) AS day,
              COALESCE(SUM(l.quantity), 0) AS drinks
            FROM drink_log l
            WHERE l.user_id = :userId
            GROUP BY ((l.consumed_at AT TIME ZONE 'UTC')::date)
            ORDER BY day
            """, nativeQuery = true)
    List<Object[]> sumDrinksPerUtcDay(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
              EXTRACT(ISOYEAR FROM (l.consumed_at AT TIME ZONE 'UTC'))::int AS iso_year,
              EXTRACT(WEEK    FROM (l.consumed_at AT TIME ZONE 'UTC'))::int AS iso_week,
              COALESCE(SUM(l.quantity), 0) AS drinks
            FROM drink_log l
            WHERE l.user_id = :userId
            GROUP BY iso_year, iso_week
            ORDER BY iso_year, iso_week
            """, nativeQuery = true)
    List<Object[]> sumDrinksPerUtcIsoWeek(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
              l.category AS category,
              COALESCE(SUM(l.quantity), 0) AS drinks
            FROM drink_log l
            WHERE l.user_id = :userId
            GROUP BY l.category
            ORDER BY drinks DESC, l.category ASC
            """, nativeQuery = true)
    List<Object[]> sumQuantityByCategory(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
              COALESCE(l.catalog_item_name, l.custom_drink_name) AS drink_name,
              l.category AS category,
              COALESCE(SUM(l.quantity), 0) AS drinks
            FROM drink_log l
            WHERE l.user_id = :userId
            GROUP BY COALESCE(l.catalog_item_name, l.custom_drink_name), l.category
            ORDER BY drinks DESC, drink_name ASC
            LIMIT 1
            """, nativeQuery = true)
    List<Object[]> findFavoriteDrink(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
              COALESCE(l.catalog_item_name, l.custom_drink_name) AS drink_name,
              l.category AS category,
              COALESCE(SUM(l.quantity), 0) AS drinks
            FROM drink_log l
            WHERE l.user_id = :userId
              AND l.consumed_at >= :fromInclusive
              AND l.consumed_at < :toExclusive
            GROUP BY COALESCE(l.catalog_item_name, l.custom_drink_name), l.category
            ORDER BY drinks DESC, drink_name ASC
            LIMIT 1
            """, nativeQuery = true)
    List<Object[]> findFavoriteDrinkBetween(
            @Param("userId") UUID userId,
            @Param("fromInclusive") Instant fromInclusive,
            @Param("toExclusive") Instant toExclusive
    );

    @Query(value = """
            SELECT
              l.category AS category,
              COALESCE(SUM(l.quantity), 0) AS drinks
            FROM drink_log l
            WHERE l.user_id = :userId
              AND l.consumed_at >= :fromInclusive
              AND l.consumed_at < :toExclusive
            GROUP BY l.category
            ORDER BY drinks DESC, l.category ASC
            LIMIT 1
            """, nativeQuery = true)
    List<Object[]> findFavoriteCategoryBetween(
            @Param("userId") UUID userId,
            @Param("fromInclusive") Instant fromInclusive,
            @Param("toExclusive") Instant toExclusive
    );

    @Query(value = """
            SELECT EXTRACT(HOUR FROM (l.consumed_at AT TIME ZONE 'UTC'))::int AS hour
            FROM drink_log l
            WHERE l.user_id = :userId
            GROUP BY hour
            ORDER BY SUM(l.quantity) DESC, hour ASC
            LIMIT 1
            """, nativeQuery = true)
    Integer findPeakHourUtc(@Param("userId") UUID userId);

    @Query(value = """
            SELECT DISTINCT ((l.consumed_at AT TIME ZONE 'UTC')::date) AS day
            FROM drink_log l
            WHERE l.user_id = :userId
            ORDER BY day DESC
            """, nativeQuery = true)
    List<LocalDate> findDistinctUtcDaysDesc(@Param("userId") UUID userId);

    @Query("""
            SELECT d FROM DrinkLog d
            WHERE d.location.latitude  IS NOT NULL
              AND d.location.longitude IS NOT NULL
              AND d.location.latitude  BETWEEN :minLat AND :maxLat
              AND d.location.longitude BETWEEN :minLon AND :maxLon
              AND d.userId IN :userIds
            """)
    List<DrinkLog> findMapLogsForUsersInBbox(
            @Param("userIds") List<UUID> userIds,
            @Param("minLat") double minLat,
            @Param("minLon") double minLon,
            @Param("maxLat") double maxLat,
            @Param("maxLon") double maxLon,
            Pageable pageable
    );

    @Query("""
            SELECT d FROM DrinkLog d
            WHERE d.location.latitude  IS NOT NULL
              AND d.location.longitude IS NOT NULL
              AND d.location.latitude  BETWEEN :minLat AND :maxLat
              AND d.location.longitude BETWEEN :minLon AND :maxLon
            """)
    List<DrinkLog> findMapLogsForAllInBbox(
            @Param("minLat") double minLat,
            @Param("minLon") double minLon,
            @Param("maxLat") double maxLat,
            @Param("maxLon") double maxLon,
            Pageable pageable
    );
}
