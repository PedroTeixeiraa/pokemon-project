INSERT INTO users (id, email, password_hash, created_at)
VALUES (1, 'demo@example.com', '$2a$10$fKdUakPsa3uHRzWuw828vOn6AsPzgwIkjXwCVMAWnnRTtNWHpR0ru', CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

INSERT INTO pokemon (poke_api_id, name, sprite_url, weight, types_json, abilities_json, stats_json,
                     description, evolution_chain_json, localized_name, region, internal_tags_json, notes, updated_at)
VALUES
    (1, 'bulbasaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png', 69,
     '["grass","poison"]', '["overgrow"]', '{"hp":45,"attack":49,"defense":49,"special-attack":65,"special-defense":65,"speed":45}',
     'A strange seed was planted on its back at birth.', '["bulbasaur","ivysaur","venusaur"]', 'Bulbasaur', 'Kanto', '["starter"]', 'Demo record', CURRENT_TIMESTAMP),
    (4, 'charmander', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png', 85,
     '["fire"]', '["blaze"]', '{"hp":39,"attack":52,"defense":43,"special-attack":60,"special-defense":50,"speed":65}',
     'The flame on its tail shows the strength of its life force.', '["charmander","charmeleon","charizard"]', 'Charmander', 'Kanto', '["starter"]', 'Demo record', CURRENT_TIMESTAMP),
    (25, 'pikachu', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png', 60,
     '["electric"]', '["static"]', '{"hp":35,"attack":55,"defense":40,"special-attack":50,"special-defense":50,"speed":90}',
     'When several Pikachu gather, their electricity could build and cause lightning storms.', '["pichu","pikachu","raichu"]', 'Pikachu', 'Kanto', '["favorite"]', 'Demo record', CURRENT_TIMESTAMP)
ON CONFLICT (poke_api_id) DO NOTHING;
