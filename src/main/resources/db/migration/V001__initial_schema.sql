create extension if not exists postgis;

create schema if not exists persons_finder;

create table persons_finder.persons (
    id bigserial primary key,
    name varchar(1000) not null
);

create table persons_finder.locations (
    reference_id bigint not null primary key,
    location geometry,
    foreign key (reference_id) references persons_finder.persons(id)
);

create index idx_location on persons_finder.locations using gist ((location::geography));
