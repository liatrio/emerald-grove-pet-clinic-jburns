import { test, expect } from '@fixtures/base-test';

test.describe('Upcoming Visits page', () => {
  test('page renders at /visits/upcoming', async ({ page }) => {
    const response = await page.goto('/visits/upcoming');
    expect(response?.status()).toBe(200);
    await expect(page.getByRole('heading', { name: /Upcoming Visits/i })).toBeVisible();
  });

  test('creates a visit and verifies it appears in upcoming list', async ({ page }) => {
    // Use a date 2 days from now so it falls in the default 7-day window
    const today = new Date();
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + 2);
    const yyyy = futureDate.getFullYear();
    const mm = String(futureDate.getMonth() + 1).padStart(2, '0');
    const dd = String(futureDate.getDate()).padStart(2, '0');
    const visitDate = `${yyyy}-${mm}-${dd}`;

    // Owner 1 (George Franklin), Pet 1 (Leo) exists in seed data
    await page.goto('/owners/1/pets/1/visits/new');
    await page.locator('input#date').fill(visitDate);
    await page.locator('input#description').fill('E2E Upcoming Visit Test');
    await page.getByRole('button', { name: /Add Visit/i }).click();

    // Verify redirect back to owner
    await expect(page).toHaveURL(/\/owners\/1/);

    // Navigate to upcoming visits
    await page.goto('/visits/upcoming');
    await expect(page.getByRole('heading', { name: /Upcoming Visits/i })).toBeVisible();

    // Verify the visit appears
    await expect(page.getByText('E2E Upcoming Visit Test')).toBeVisible();
    await expect(page.getByText(visitDate)).toBeVisible();
  });

  test('respects days query parameter', async ({ page }) => {
    const response = await page.goto('/visits/upcoming?days=14');
    expect(response?.status()).toBe(200);
    await expect(page.getByText(/14/)).toBeVisible();
  });
});
