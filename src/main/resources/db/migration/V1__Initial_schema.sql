CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    publication_date DATE NOT NULL
);

CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL
);

CREATE TABLE book_copies (
    book_id BIGINT,
    location_id BIGINT,
    quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (book_id, location_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE INDEX idx_book_title ON books (title);
CREATE INDEX idx_book_author_title ON books (author, title);