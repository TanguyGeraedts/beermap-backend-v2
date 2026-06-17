ALTER TABLE draft_logs
ALTER COLUMN captured_at TYPE TIMESTAMP WITH TIME ZONE;

DROP INDEX IF EXISTS idx_draft_user_time;
CREATE INDEX idx_draft_user_time ON draft_logs (user_id, captured_at);