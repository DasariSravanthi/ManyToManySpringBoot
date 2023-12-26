create table if not exists event (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(255),
    date varchar(255)
);

create table if not exists sponsor (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(255),
    industry varchar(255)
);

create table if not exists event_sponsor (
    eventId INT,
    sponsorId INT,
    PRIMARY KEY (eventId, sponsorId),
    FOREIGN KEY (eventId) REFERENCES event(id),
    FOREIGN KEY (sponsorId) REFERENCES sponsor(id)
);