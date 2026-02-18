import { test, expect } from '@fixtures/base-test';

test.describe('Friendly 404 pages', () => {
  test('navigating to a non-existent owner shows 404 page with friendly message', async ({ page }) => {
    const response = await page.goto('/owners/99999');

    // Verify HTTP 404 status
    expect(response?.status()).toBe(404);

    // Verify friendly heading is shown (not a stack trace)
    await expect(page.getByRole('heading', { level: 2 })).toContainText(/something happened/i);

    // Verify "Find Owners" link is present in the error card (in addition to nav)
    await expect(page.locator('.liatrio-error-card').getByRole('link', { name: /Find Owners/i })).toBeVisible();

    // Verify no internal exception details are exposed
    const bodyText = await page.locator('body').textContent();
    expect(bodyText).not.toContain('IllegalArgumentException');
    expect(bodyText).not.toContain('at org.springframework');
    expect(bodyText).not.toContain('Owner not found with id');
  });

  test('Find Owners link on 404 page navigates to owner search', async ({ page }) => {
    await page.goto('/owners/99999');

    // Click Find Owners link and verify navigation
    await page.getByRole('link', { name: /Find Owners/i }).first().click();
    await expect(page).toHaveURL(/\/owners\/find/);
  });
});
