package be.tanguy.beermap.api.logs.service;

public interface DraftRetentionService {
    void cleanupExpiredDrafts();
}