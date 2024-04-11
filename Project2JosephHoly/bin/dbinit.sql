use jholy2;

drop table if exists Posts;
drop table if exists Visibility;
drop table if exists Users;

create table Users (
userID int auto_increment,
username varchar(50) unique not null,
password varchar(50) unique not null,
primary key (userID)
);

create table Visibility (
userID int not null,
visibleID int not null,
primary key (userID, visibleID),
foreign key (userID) references Users (userID),
foreign key (visibleID) references Users (userID)
);

create table Posts (
postID int auto_increment,
postText varchar(255) not null,
postTime varchar(255) not null,
userID int not null,
primary key (postID),
foreign key (userID) references Users (userID)
);

insert into Users (username, password) values
("Alice", "Alice123"),
("Bob", "Bob123"),
("Crystal", "Crystal123"),
("David", "David123");

insert into Visibility (userID, visibleID) values
(1,2), (1,3), (2,1), (2,3), (3,1), (1,1), (2,2), (3,3), (4,4);

delimiter //
create trigger after_user_insert
after insert on Users
for each row
begin
	insert into Visibility (userID, visibleID) 
    values (NEW.userID, NEW.userID);
end; 
//
delimiter ;

insert into Posts (postText, postTime, userID) values
("Project deadline extended?", "2023-10-12 19:00:00", 1),
("Yep", "2023-10-12 19:01:00", 2),
("Fall break","2023-10-16 09:00:00", 4),
("Lab due tonight?", "2023-10-27 23:30:00", 1),
("No, it's due next week","2023-10-27 23:35:00", 3);
