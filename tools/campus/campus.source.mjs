// Source of the fictional campus, read by generate.mjs.
//
// Units: plan pixels, 1 px = 0.07 m. Each building has its own plan (0,0 = top-left corner of the
// building); `origin` places that plan on the site plan (the outdoor space "EXT").
//
// Room: [code, x, y, width, height, doorSide, typeKey, overrides?]
//   doorSide = wall of the door: N (top), S (bottom), W (left), E (right). The door is in the middle
//   of that wall unless overrides.door gives another fraction (0.2 = 20 % along the wall).
//   overrides may also set name, cap, eq, tag. Default name = "<type> <code>".
// Corridors: polylines of junction names. A name shared by two polylines is a junction (T, L...).
// Stairs / elevators: a rectangle with a door, present on every floor of the building unless
// `floors` is given. Building doors: a point on the outline, linked to an outdoor point.
// Outdoor paths: [from, to, weight, kind?]; weight is explicit (>= straight-line distance).

export default {
  scale: 0.07,
  vertical: { stairs: 140, elevator: 90 }, // weight of one floor change

  types: {
    td: { type: 'Salle de TD', tag: 'TD', cap: 30, eq: ['Tableau', 'Vidéoprojecteur'] },
    tp: { type: 'Salle de TP', tag: 'TP', cap: 24, eq: ['Paillasses', 'Hotte', 'Tableau'] },
    labo: { type: 'Laboratoire', tag: 'Labo', cap: 20, eq: ['Postes de mesure', 'Vidéoprojecteur'] },
    info: { type: 'Salle informatique', tag: 'Info', cap: 30, eq: ['30 PC', 'Vidéoprojecteur'] },
    amphi: { type: 'Amphithéâtre', tag: 'Amphi', cap: 200, eq: ['Vidéoprojecteur', 'Sonorisation', 'Prises'] },
    reunion: { type: 'Salle de réunion', tag: 'Réunion', cap: 12, eq: ['Écran', 'Visioconférence'] },
    secretariat: { type: 'Secrétariat', tag: 'Secrétariat', cap: 6, eq: ['Accueil'] },
    scolarite: { type: 'Scolarité', tag: 'Scolarité', cap: 8, eq: ['Accueil', 'Guichets'] },
    accueil: { type: 'Accueil', tag: 'Accueil', cap: 4, eq: ['Accueil', 'Plans du campus'] },
    bureau: { type: 'Bureau', tag: 'Bureau', cap: 6, eq: ['Postes de travail'] },
    cafeteria: { type: 'Cafétéria', tag: 'Cafétéria', cap: 80, eq: ['Distributeurs', 'Micro-ondes'] },
    service: { type: 'Service', tag: 'Service', cap: 4, eq: ['Photocopieurs'] },
    biblio: { type: 'Bibliothèque', tag: 'Biblio', cap: 150, eq: ['Rayonnages', 'Postes de consultation', 'Wi-Fi'] },
    lecture: { type: 'Salle de lecture', tag: 'Lecture', cap: 60, eq: ['Tables', 'Prises', 'Wi-Fi'] },
    travail: { type: 'Salle de travail en groupe', tag: 'Groupe', cap: 8, eq: ['Écran', 'Tableau blanc'] },
    formation: { type: 'Salle de formation', tag: 'Formation', cap: 20, eq: ['Vidéoprojecteur', '20 PC'] },
  },

  buildings: [
    // ------------------------------------------------------------ A: Sciences, L-shaped
    // North wing (y 0-300) + east wing (x 700-1000). Corridor: west-east along the north wing,
    // T junction K, then south along the east wing. On the RDC a short branch V-G leads to the
    // courtyard door D2.
    {
      code: 'A',
      name: 'Bâtiment A – Sciences',
      size: [1000, 800],
      origin: [200, 200],
      outline: [[0, 0], [1000, 0], [1000, 800], [700, 800], [700, 300], [0, 300]],
      junctions: { W: [30, 150], K: [850, 150], X: [950, 150], V: [850, 560], S: [850, 720], G: [730, 560] },
      stairs: [
        { id: 'S1', rect: [20, 180, 60, 100], door: 'N' },
        { id: 'S2', rect: [820, 750, 60, 40], door: 'N' },
      ],
      elevators: [{ id: 'E', rect: [880, 190, 60, 60], door: 'W' }],
      doors: [
        { id: 'D1', floor: 0, at: [0, 150], label: 'Bâtiment A – porte ouest', outdoor: 'PAW', w: 40 },
        { id: 'D2', floor: 0, at: [700, 560], label: 'Bâtiment A – porte cour', outdoor: 'PA', w: 40 },
      ],
      floors: {
        0: {
          corridors: [['W', 'K', 'X'], ['K', 'V', 'S'], ['V', 'G']],
          rooms: [
            ['A001', 20, 20, 200, 100, 'S', 'tp', { name: 'TP de chimie' }],
            ['A002', 230, 20, 200, 100, 'S', 'tp', { name: 'TP de biologie' }],
            ['A003', 440, 20, 180, 100, 'S', 'info'],
            ['A004', 630, 20, 160, 100, 'S', 'secretariat', { name: 'Secrétariat des sciences' }],
            ['A005', 800, 20, 180, 100, 'S', 'td'],
            ['A006', 90, 180, 180, 100, 'N', 'td'],
            ['A007', 280, 180, 240, 100, 'N', 'labo', { name: 'Laboratoire de physique' }],
            ['A008', 530, 180, 150, 100, 'N', 'reunion', { name: 'Salle de réunion des sciences' }],
            ['A009', 690, 180, 120, 100, 'N', 'td'],
            ['A010', 720, 320, 100, 200, 'E', 'td'],
            ['A011', 880, 270, 100, 220, 'W', 'info'],
            ['A012', 880, 500, 100, 240, 'W', 'labo', { name: "Laboratoire d'électronique" }],
          ],
        },
        1: {
          corridors: [['W', 'K', 'X'], ['K', 'V', 'S']],
          rooms: [
            ['A101', 20, 20, 180, 100, 'S', 'td'],
            ['A102', 210, 20, 180, 100, 'S', 'td'],
            ['A103', 400, 20, 250, 100, 'S', 'labo', { name: 'Laboratoire de chimie organique' }],
            ['A104', 660, 20, 320, 100, 'S', 'labo', { name: 'Laboratoire de biologie cellulaire' }],
            ['A105', 90, 180, 200, 100, 'N', 'td'],
            ['A106', 300, 180, 200, 100, 'N', 'info'],
            ['A107', 510, 180, 300, 100, 'N', 'td', { cap: 40 }],
            ['A108', 720, 320, 100, 200, 'E', 'td'],
            ['A109', 720, 540, 100, 200, 'E', 'reunion'],
            ['A110', 880, 270, 100, 470, 'W', 'labo', { name: 'Laboratoire de mathématiques appliquées' }],
          ],
        },
        2: {
          corridors: [['W', 'K', 'X'], ['K', 'V', 'S']],
          rooms: [
            ['A201', 20, 20, 240, 100, 'S', 'td'],
            ['A202', 270, 20, 240, 100, 'S', 'td'],
            ['A203', 520, 20, 220, 100, 'S', 'info'],
            ['A204', 750, 20, 230, 100, 'S', 'td'],
            ['A205', 90, 180, 240, 100, 'N', 'reunion'],
            ['A206', 340, 180, 240, 100, 'N', 'labo', { name: "Laboratoire d'informatique" }],
            ['A207', 590, 180, 220, 100, 'N', 'td'],
            ['A208', 720, 320, 100, 420, 'E', 'td', { cap: 40 }],
            ['A209', 880, 270, 100, 220, 'W', 'tp', { name: 'TP de physique' }],
            ['A210', 880, 500, 100, 240, 'W', 'bureau', { name: 'Bureau des enseignants-chercheurs' }],
          ],
        },
      },
    },

    // ------------------------------------------------------------ B: Amphithéâtres et administration
    // Rectangle with a central hall. RDC: cross-shaped corridors meeting at the hall (H), main door
    // D1 to the south (towards ENTREE), door D2 to the north (courtyard). Floor 1: a gallery ring
    // around the hall void, with corridors to the west and east wings.
    {
      code: 'B',
      name: 'Bâtiment B – Amphithéâtres et administration',
      size: [900, 500],
      origin: [1100, 1300],
      outline: [[0, 0], [900, 0], [900, 500], [0, 500]],
      junctions: {
        BW: [30, 250], H: [450, 250], BE: [830, 250], HN: [450, 30], HS: [450, 470],
        NW: [350, 100], NE: [550, 100], GE: [550, 250], SE: [550, 400], SW: [350, 400], GW: [350, 250],
      },
      stairs: [
        { id: 'S1', rect: [370, 20, 60, 50], door: 'S' },
        { id: 'S2', rect: [850, 220, 40, 60], door: 'W' },
      ],
      elevators: [{ id: 'E', rect: [470, 20, 60, 50], door: 'S' }],
      doors: [
        { id: 'D1', floor: 0, at: [450, 500], label: 'Bâtiment B – entrée principale', outdoor: 'P1', w: 40 },
        { id: 'D2', floor: 0, at: [450, 0], label: 'Bâtiment B – porte cour', outdoor: 'P3', w: 40 },
      ],
      floors: {
        0: {
          corridors: [['BW', 'H', 'BE'], ['HN', 'H', 'HS']],
          rooms: [
            ['B001', 20, 20, 300, 200, 'S', 'amphi', { name: 'Amphithéâtre Turing', cap: 250 }],
            ['B002', 20, 280, 140, 200, 'N', 'accueil', { name: 'Accueil du campus' }],
            ['B003', 170, 280, 150, 200, 'N', 'scolarite', { name: 'Scolarité' }],
            ['B004', 580, 20, 270, 200, 'S', 'amphi', { name: 'Amphithéâtre Lovelace', cap: 180 }],
            ['B005', 580, 280, 180, 200, 'N', 'cafeteria', { name: 'Cafétéria' }],
            ['B006', 770, 280, 90, 200, 'N', 'secretariat', { name: 'Secrétariat pédagogique' }],
            ['B007', 340, 380, 90, 90, 'E', 'service', { name: 'Reprographie' }],
            ['B008', 470, 380, 90, 90, 'W', 'bureau', { name: 'Bureau des associations' }],
          ],
        },
        1: {
          corridors: [['NW', 'NE', 'GE', 'SE', 'SW', 'GW', 'NW'], ['BW', 'GW'], ['GE', 'BE']],
          rooms: [
            ['B101', 20, 20, 300, 200, 'S', 'reunion', { name: 'Salle du conseil', cap: 40 }],
            ['B102', 20, 280, 150, 200, 'N', 'bureau', { name: 'Direction du campus' }],
            ['B103', 180, 280, 140, 200, 'N', 'bureau', { name: 'Ressources humaines' }],
            ['B104', 580, 20, 140, 200, 'S', 'bureau', { name: 'Relations internationales' }],
            ['B105', 730, 20, 120, 200, 'S', 'reunion'],
            ['B106', 580, 280, 140, 200, 'N', 'bureau', { name: 'Service financier' }],
            ['B107', 730, 280, 120, 200, 'N', 'bureau', { name: 'Bureau des enseignants' }],
            ['B108', 380, 420, 140, 60, 'N', 'reunion', { name: 'Salle de visioconférence' }],
          ],
        },
      },
    },

    // ------------------------------------------------------------ C: Bibliothèque / learning center
    // U-shaped around an atrium open to the north: west wing, south base, east wing. The only
    // elevator is at the north end of the WEST wing, so an accessible route to the east wing is
    // much longer than the route by the east stairs S2. The door D1 is in the middle of the base.
    {
      code: 'C',
      name: 'Bâtiment C – Bibliothèque',
      size: [900, 700],
      origin: [1900, 200],
      outline: [[0, 0], [300, 0], [300, 450], [600, 450], [600, 0], [900, 0], [900, 700], [0, 700]],
      junctions: { WN: [150, 80], WS: [150, 575], MS: [450, 575], ES: [750, 575], EN: [750, 80], MD: [450, 670] },
      stairs: [
        { id: 'S1', rect: [120, 10, 60, 40], door: 'S' },
        { id: 'S2', rect: [720, 10, 60, 40], door: 'S' },
      ],
      elevators: [{ id: 'E', rect: [180, 90, 60, 60], door: 'W' }],
      doors: [{ id: 'D1', floor: 0, at: [450, 700], label: 'Bâtiment C – entrée', outdoor: 'P6', w: 40 }],
      floors: {
        0: {
          corridors: [['WN', 'WS', 'MS', 'ES', 'EN'], ['MS', 'MD']],
          rooms: [
            ['C001', 10, 90, 110, 455, 'E', 'biblio', { name: 'Bibliothèque universitaire', cap: 200 }],
            ['C002', 180, 160, 110, 150, 'W', 'accueil', { name: 'Accueil de la bibliothèque' }],
            ['C003', 180, 320, 110, 120, 'W', 'travail'],
            ['C004', 310, 460, 180, 85, 'S', 'info'],
            ['C005', 500, 460, 220, 85, 'S', 'lecture'],
            ['C006', 10, 605, 300, 85, 'N', 'biblio', { name: 'Médiathèque', cap: 60 }],
            ['C007', 320, 605, 90, 85, 'N', 'service', { name: "Point d'impression" }],
            ['C008', 490, 605, 150, 85, 'N', 'formation'],
            ['C009', 650, 605, 190, 85, 'N', 'travail'],
            ['C010', 610, 160, 110, 280, 'E', 'travail'],
            ['C011', 780, 90, 110, 455, 'W', 'lecture', { name: 'Espace presse' }],
          ],
        },
        1: {
          corridors: [['WN', 'WS', 'MS', 'ES', 'EN']],
          rooms: [
            ['C101', 10, 90, 110, 455, 'E', 'lecture', { name: 'Salle de lecture silencieuse', cap: 80 }],
            ['C102', 180, 160, 110, 130, 'W', 'travail'],
            ['C103', 180, 300, 110, 140, 'W', 'travail'],
            ['C104', 190, 460, 250, 85, 'S', 'info'],
            ['C105', 450, 460, 270, 85, 'S', 'formation'],
            ['C106', 10, 605, 440, 85, 'N', 'biblio', { name: 'Fonds patrimoniaux', cap: 40 }],
            ['C107', 460, 605, 430, 85, 'N', 'travail', { name: 'Espace de coworking', cap: 40 }],
            ['C108', 610, 160, 110, 280, 'E', 'travail'],
            ['C109', 780, 90, 110, 455, 'W', 'lecture'],
          ],
        },
        2: {
          corridors: [['WN', 'WS', 'MS', 'ES', 'EN']],
          rooms: [
            ['C201', 10, 90, 110, 455, 'E', 'lecture'],
            ['C202', 180, 160, 110, 280, 'W', 'reunion'],
            ['C203', 190, 460, 260, 85, 'S', 'info'],
            ['C204', 460, 460, 260, 85, 'S', 'travail'],
            ['C205', 10, 605, 440, 85, 'N', 'formation'],
            ['C206', 460, 605, 240, 85, 'N', 'travail'],
            ['C207', 710, 605, 180, 85, 'N', 'bureau', { name: 'Bureau des bibliothécaires', door: 0.2 }],
            ['C208', 610, 160, 110, 280, 'E', 'travail'],
            ['C209', 780, 90, 110, 455, 'W', 'labo', { name: 'Laboratoire de langues' }],
          ],
        },
      },
    },
  ],

  // -------------------------------------------------------------- outdoor space (site plan)
  // ENTREE (south) -> parvis P1 -> through building B or around it (PS) -> courtyard P2.
  // Building C stands on a terrace: steps P4-P5 (stairs), or the longer ramp P4-R1-R2-P5.
  outdoor: {
    name: 'Campus (extérieur)',
    size: [3000, 2000],
    points: {
      ENTREE: [1550, 1960, 'Entrée du campus', 'entrance'],
      P1: [1550, 1840, 'Parvis du bâtiment B'],
      PS: [700, 1840, 'Allée sud-ouest'],
      PC1: [860, 1100, 'Cour – angle ouest'],
      PA: [860, 760, 'Cour – côté A'],
      PW: [160, 1100, 'Allée ouest'],
      PAW: [160, 350, 'Allée du bâtiment A'],
      P2: [1550, 1100, 'Cour centrale'],
      P3: [1550, 1260, 'Cour – côté B'],
      P4: [2350, 1100, 'Bas des marches'],
      P5: [2350, 1000, 'Haut des marches'],
      R1: [2650, 1100, "Rampe d'accès (bas)"],
      R2: [2650, 1000, "Rampe d'accès (haut)"],
      P6: [2350, 940, 'Parvis du bâtiment C'],
    },
    paths: [
      ['ENTREE', 'P1', 120],
      ['P1', 'PS', 850],
      ['PS', 'PC1', 760],
      ['PC1', 'PA', 340],
      ['PC1', 'PW', 700],
      ['PW', 'PAW', 750],
      ['PC1', 'P2', 690],
      ['P2', 'P3', 160],
      ['P2', 'P4', 800],
      ['P4', 'P5', 100, 'stairs'],
      ['P4', 'R1', 300],
      ['R1', 'R2', 100],
      ['R2', 'P5', 300],
      ['P5', 'P6', 60],
    ],
  },
}
