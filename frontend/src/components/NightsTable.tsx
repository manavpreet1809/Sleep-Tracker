import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import type { Night, api } from '../api'
import { clock, day, hoursAndMinutes } from '../format'

const PAGE = 10

export function NightsTable({ nights, client, onEdit, onChanged }: {
  nights: Night[]
  client: ReturnType<typeof api>
  onEdit: (night: Night) => void
  onChanged: () => void
}) {
  const [showAll, setShowAll] = useState(false)
  const remove = useMutation({ mutationFn: client.deleteNight, onSuccess: onChanged })
  const newestFirst = [...nights].reverse()
  const shown = showAll ? newestFirst : newestFirst.slice(0, PAGE)
  return (
    <section className="card">
      <h2>Your nights</h2>
      <div className="table-scroll">
        <table>
          <thead>
            <tr><th>Night</th><th>Quality</th><th>Asleep</th><th>Schedule</th><th><span className="sr-only">Actions</span></th></tr>
          </thead>
          <tbody>
            {shown.map(night => (
              <tr key={night.date}>
                <td>
                  {day(night.date)}
                  {night.source === 'garmin' && <span className="badge" title="Imported from Garmin">Garmin</span>}
                </td>
                <td><span className={`pill q${Math.round(night.quality / 3.4)}`}>{night.quality}</span></td>
                <td>{hoursAndMinutes(night.sleepHours)}</td>
                <td className="muted">{clock(night.bedtime)}–{clock(night.wakeTime)}</td>
                <td className="row-actions">
                  <button className="link" onClick={() => onEdit(night)}>Edit</button>
                  <button className="link danger" disabled={remove.isPending}
                    onClick={() => window.confirm(`Delete the night of ${night.date}?`) && remove.mutate(night.date)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {nights.length > PAGE && (
        <button className="link" onClick={() => setShowAll(!showAll)}>
          {showAll ? 'Show fewer' : `Show all ${nights.length} nights`}
        </button>
      )}
    </section>
  )
}
