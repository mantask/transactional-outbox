-- Conforms to the default Debezium structure
-- https://debezium.io/documentation/reference/1.2/configuration/outbox-event-router.html#basic-outbox-table

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE msg_outbox (
  id UUID primary key default uuid_generate_v4(),
  aggregatetype text not null,
  aggregateid UUID not null,
  type text not null,
  payload text not null
);

CREATE TABLE msg_inbox_dlq (
  id UUID primary key default uuid_generate_v4(),
  aggregatetype text not null,
  aggregateid UUID not null,
  type text not null,
  payload text not null,
  ts timestamptz not null
);

CREATE TABLE msg_inbox (
  id UUID primary key default uuid_generate_v4(),
  created_on timestamptz not null
);
