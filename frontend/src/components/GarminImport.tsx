import { useRef } from 'react'
import { useMutation } from '@tanstack/react-query'
import type { api } from '../api'

/** Uploads a Garmin Connect sleep export. Nights already logged keep their habits. */
export function GarminImport({ client, onImported }: { client: ReturnType<typeof api>; onImported: () => void }) {
  const input = useRef<HTMLInputElement>(null)
  const upload = useMutation({ mutationFn: client.importGarmin, onSuccess: onImported })
  const result = upload.data
  return (
    <section className="card">
      <h2>Import from Garmin</h2>
      <p className="muted">
        Export your sleep data from Garmin Connect as a CSV file and upload it here. Habits you already typed in are
        kept.
      </p>
      <div className="actions">
        <input
          ref={input} type="file" accept=".csv,text/csv" className="sr-only" aria-label="Garmin sleep CSV file"
          onChange={e => { const file = e.target.files?.[0]; if (file) upload.mutate(file); e.target.value = '' }}
        />
        <button className="secondary" onClick={() => input.current?.click()} disabled={upload.isPending}>
          {upload.isPending ? 'Importing…' : 'Choose CSV file'}
        </button>
      </div>
      {result && (
        <p role="status">
          Added {result.added} and updated {result.updated} nights.
          {result.skipped.length > 0 && ` Skipped ${result.skipped.length} ${result.skipped.length === 1 ? 'row' : 'rows'} with no usable sleep data.`}
        </p>
      )}
      {upload.isError && <p className="error" role="alert">{upload.error.message}</p>}
    </section>
  )
}
