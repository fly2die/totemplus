# Totem+

Five new totems for Minecraft 26.3 (Fabric). Each one activates on lethal damage instead of
death, restores its own amount of health and applies its own effect.

## Totems

| Totem | Health restored | Effect |
| --- | --- | --- |
| Angel Totem | 5 hearts | Strength II + Speed II (15s), Regeneration I (20s), levitation on hostile mobs within 8 blocks |
| Devil Totem | 3 hearts | Strength II + Speed II (15s), blood harvest: 4 hearts of damage to hostile mobs within 8 blocks, heals for every victim |
| Storm Totem | 5 hearts | Speed III, Regeneration I, Resistance I, Haste I (15s), lightning strikes up to 8 hostile mobs within 15 blocks |
| Berserker Totem | 2 hearts | Strength IV, Speed III, Haste III, Resistance I (15s) |
| Guardian Totem | 6 hearts | Resistance III + Regeneration II (20s), Absorption IV (30s), removes all harmful effects, Weakness II on hostile mobs within 10 blocks |

All totems are shaped recipes around a vanilla Totem of Undying — see
`src/main/resources/data/totemplus/recipe/`.

Every totem also grants a short second chance: Regeneration II and Absorption II for 5 seconds
plus Fire Resistance for 10 seconds. Without it the player would die to the next hit.

Totems work on hostile mobs only: pets, villagers, golems and other peaceful entities are
never affected.

## Building

```
./gradlew build
```

The mod jar ends up in `build/libs/`.

## License

MIT License

Copyright (c) 2026 f2d

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
