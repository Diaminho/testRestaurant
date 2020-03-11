CREATE TABLE recipes (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE,
  description VARCHAR(100) NOT NULL
);

CREATE TABLE ingredients (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE,
  recipe_id INT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);