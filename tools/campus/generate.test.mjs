// Run with: node --test tools/campus/generate.test.mjs
import assert from 'node:assert/strict'
import { test } from 'node:test'

import { buildGraph, projectOnSegment } from './generate.mjs'
import { validate } from './validate.mjs'

// Smallest valid campus: one building T (one floor, one corridor W-E, two rooms), one door,
// and two outdoor points (ENTREE and P1 in front of the door).
function tinySource() {
  return {
    scale: 0.07,
    vertical: { stairs: 140, elevator: 90 },
    types: { td: { type: 'Salle de TD', tag: 'TD', cap: 30, eq: ['Tableau'] } },
    buildings: [{
      code: 'T',
      name: 'Bâtiment T',
      size: [400, 200],
      origin: [100, 50],
      outline: [[0, 0], [400, 0], [400, 200], [0, 200]],
      junctions: { W: [20, 100], E: [380, 100] },
      doors: [{ id: 'D1', floor: 0, at: [0, 100], label: 'Porte', outdoor: 'P1', w: 40 }],
      floors: {
        0: {
          corridors: [['W', 'E']],
          rooms: [
            ['T001', 50, 10, 100, 60, 'S', 'td'],
            ['T002', 200, 10, 100, 60, 'S', 'td'],
          ],
        },
      },
    }],
    outdoor: {
      name: 'Extérieur',
      size: [600, 400],
      points: { ENTREE: [60, 280, 'Entrée', 'entrance'], P1: [60, 150, 'Parvis'] },
      paths: [['ENTREE', 'P1', 130]],
    },
  }
}

function allErrors(source) {
  const { graph, errors } = buildGraph(source)
  return [...errors, ...validate(graph)]
}

test('projection on a segment is clamped to its ends', () => {
  assert.deepEqual(projectOnSegment([5, 5], [0, 0], [10, 0]), { t: 0.5, point: [5, 0] })
  assert.deepEqual(projectOnSegment([20, 3], [0, 0], [10, 0]), { t: 1, point: [10, 0] })
})

test('a tiny valid source gives a valid graph', () => {
  const { graph, errors } = buildGraph(tinySource())
  assert.deepEqual([...errors, ...validate(graph)], [])
  // T001's door (100,70) is linked to the point in front of it on the corridor (100,100)
  assert.ok(graph.edges.some((e) => e.a === 'T001' && e.b === 'T001_C' && e.w === 30))
})

test('a duplicate room code is reported', () => {
  const source = tinySource()
  source.buildings[0].floors[0].rooms[1][0] = 'T001'
  assert.ok(allErrors(source).includes('duplicate room code T001'))
})

test('an isolated node is reported', () => {
  const source = tinySource()
  source.outdoor.points.P9 = [500, 300, 'Nulle part']
  assert.ok(allErrors(source).includes('node EXT-P9 is isolated'))
})
