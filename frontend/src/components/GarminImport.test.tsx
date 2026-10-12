import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ApiError, type api } from '../api'
import { GarminImport } from './GarminImport'

function setup(importGarmin: (file: File) => Promise<unknown>) {
  const onImported = vi.fn()
  render(
    <QueryClientProvider client={new QueryClient()}>
      <GarminImport client={{ importGarmin } as unknown as ReturnType<typeof api>} onImported={onImported} />
    </QueryClientProvider>,
  )
  return { onImported }
}

const csv = () => new File(['Score,Duration,Bedtime,Wake Time\n'], 'sleep.csv', { type: 'text/csv' })

test('uploads the chosen file and reports the result', async () => {
  const importGarmin = vi.fn(async () => ({ added: 26, updated: 1, skipped: [{ line: 2, reason: 'No sleep recorded' }] }))
  const { onImported } = setup(importGarmin)
  await userEvent.setup().upload(screen.getByLabelText('Garmin sleep CSV file'), csv())
  expect(await screen.findByText('Added 26 and updated 1 nights. Skipped 1 row with no usable sleep data.')).toBeInTheDocument()
  expect(importGarmin).toHaveBeenCalledWith(expect.any(File), expect.anything())
  expect(onImported).toHaveBeenCalled()
})

test('shows why a file was rejected', async () => {
  setup(async () => { throw new ApiError(400, 'This doesn’t look like a Garmin sleep export') })
  await userEvent.setup().upload(screen.getByLabelText('Garmin sleep CSV file'), csv())
  expect(await screen.findByRole('alert')).toHaveTextContent('This doesn’t look like a Garmin sleep export')
})
