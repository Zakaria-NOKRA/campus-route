// Decoration of the campus plan (EXT): lawns, trees, flowerbeds, benches, fountain.
// Purely visual: the API knows nothing about it, nothing here is used to compute a route.
//
// Coordinates are EXT plan pixels (site 3000 x 2000), placed by hand in the free spaces of
// tools/campus/campus.source.mjs: buildings A (200..1200, 200..1000, L shape), B
// (1100..2000, 1300..1800), C (1900..2800, 200..900, U open to the north), and the paths
// (60 px wide) around y = 1100, x = 160, x = 860, y = 1840, the diagonal PS-PC1 and the ramp.
// If the campus data changes, only these lists may need moving.
// Fixed lists = the same picture at every render (no random).

// Darker lawns (rounded rectangles) on the grass.
export const LAWNS = [
  { x: 215, y: 540, width: 595, height: 505 }, // west lawn, inside the L of building A
  { x: 1250, y: 250, width: 600, height: 790 }, // central courtyard, between A and C
  { x: 30, y: 1160, width: 600, height: 620 }, // south-west park
  { x: 2060, y: 1160, width: 920, height: 820 }, // east park
  { x: 2215, y: 205, width: 270, height: 440 }, // garden inside the U of building C
]

// Trees: [x, y, radius, shade]. Shade 1, 2 or 3 picks one of three greens (--tree-1..3).
export const TREES = [
  // row along the north edge
  [260, 95, 40, 2], [470, 95, 36, 1], [690, 95, 42, 3], [900, 95, 38, 2], [1120, 95, 40, 1],
  [1340, 95, 36, 3], [1560, 95, 42, 2], [1780, 95, 38, 1], [2000, 95, 40, 3], [2240, 95, 36, 2],
  [2460, 95, 42, 1], [2680, 95, 38, 3], [2900, 95, 40, 2],
  // row along the west edge
  [70, 300, 36, 1], [70, 480, 40, 3], [70, 660, 36, 2], [70, 840, 40, 1], [70, 1020, 36, 3],
  // west lawn
  [300, 620, 48, 2], [430, 700, 40, 1], [330, 860, 55, 3], [560, 610, 38, 2], [720, 940, 42, 1],
  // central courtyard, around the fountain
  [1330, 340, 50, 1], [1470, 300, 40, 3], [1640, 330, 46, 2], [1780, 380, 40, 1],
  [1310, 560, 44, 2], [1790, 600, 48, 3], [1330, 980, 36, 1], [1790, 980, 38, 2],
  // south-west park
  [110, 1220, 46, 1], [260, 1260, 38, 3], [420, 1210, 50, 2], [560, 1290, 40, 1],
  [130, 1420, 52, 2], [330, 1450, 44, 1], [520, 1480, 48, 3], [100, 1650, 40, 3],
  [560, 1700, 46, 2], [330, 1720, 36, 1],
  // between the diagonal path and building B
  [960, 1420, 44, 2], [980, 1600, 40, 1], [950, 1740, 34, 3],
  // small trees along the south edge
  [120, 1925, 26, 1], [320, 1925, 24, 3], [520, 1925, 26, 2], [720, 1925, 24, 1],
  [920, 1925, 26, 3], [1120, 1925, 24, 2], [1320, 1925, 26, 1],
  // east park
  [2170, 1330, 50, 1], [2320, 1220, 40, 3], [2500, 1280, 54, 2], [2700, 1230, 42, 1],
  [2880, 1280, 48, 3], [2120, 1480, 44, 2], [2860, 1500, 52, 1], [2200, 1700, 48, 3],
  [2420, 1820, 40, 1], [2650, 1760, 56, 2], [2880, 1860, 44, 3], [2100, 1900, 36, 1],
  // row along the east edge
  [2920, 300, 38, 2], [2920, 480, 42, 1], [2920, 660, 38, 3], [2920, 840, 42, 2], [2920, 1020, 36, 1],
  // garden of building C
  [2280, 300, 40, 2], [2420, 540, 44, 1],
]

// Flowerbeds: a rounded bed of soil with a row of flowers.
export const FLOWERBEDS = [
  { x: 470, y: 860, width: 200, height: 60 }, // west lawn
  { x: 1310, y: 800, width: 110, height: 44 }, // left of the fountain
  { x: 1680, y: 800, width: 110, height: 44 }, // right of the fountain
  { x: 950, y: 1180, width: 180, height: 50 }, // courtyard, along building B
  { x: 1300, y: 1180, width: 180, height: 50 },
  { x: 1650, y: 1180, width: 200, height: 50 },
  { x: 2050, y: 1180, width: 200, height: 50 },
  { x: 220, y: 1580, width: 220, height: 60 }, // south-west park
  { x: 2350, y: 1480, width: 260, height: 70 }, // east park
  { x: 2240, y: 400, width: 140, height: 44 }, // garden of building C
  { x: 1960, y: 950, width: 200, height: 50 }, // in front of building C
]

// Benches: small rectangles (wider than tall = seen from above, along a path).
export const BENCHES = [
  { x: 520, y: 1000, width: 60, height: 16 },
  { x: 1500, y: 960, width: 100, height: 18 }, // facing the fountain
  { x: 1180, y: 1200, width: 60, height: 16 },
  { x: 1900, y: 1200, width: 70, height: 16 },
  { x: 470, y: 1600, width: 60, height: 16 },
  { x: 2400, y: 1600, width: 70, height: 16 },
  { x: 2560, y: 1600, width: 70, height: 16 },
]

// Fountain in the middle of the central courtyard, north of the "Cour centrale" point.
export const FOUNTAIN = { x: 1550, y: 820, r: 85 }

// Positions of the flowers in a bed: one row, evenly spaced, a little up and down in turn,
// colours 1 and 2 in turn. Computed from the bed size, so always the same result.
export function flowerDots(bed) {
  const count = Math.max(3, Math.round(bed.width / 36))
  const dots = []
  for (let i = 0; i < count; i++) {
    dots.push({
      x: bed.x + ((i + 0.5) * bed.width) / count,
      y: bed.y + bed.height / 2 + (i % 2 === 0 ? -bed.height * 0.15 : bed.height * 0.15),
      color: (i % 2) + 1,
    })
  }
  return dots
}
