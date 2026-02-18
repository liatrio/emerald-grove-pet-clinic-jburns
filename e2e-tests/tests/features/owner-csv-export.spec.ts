import { test, expect } from '@fixtures/base-test';

test.describe('Owner CSV Export', () => {
  test('downloads CSV with correct headers and data', async ({ page }) => {
    const response = await page.request.get('/owners.csv?lastName=Davis');

    expect(response.status()).toBe(200);
    expect(response.headers()['content-type']).toContain('text/csv');
    expect(response.headers()['content-disposition']).toContain('owners.csv');

    let content = await response.text();
    // Strip UTF-8 BOM if present
    if (content.charCodeAt(0) === 0xfeff) {
      content = content.slice(1);
    }

    const lines = content.trim().split(/\r?\n/);
    expect(lines[0]).toContain('Name');
    expect(lines[0]).toContain('Address');
    expect(lines[0]).toContain('City');
    expect(lines[0]).toContain('Telephone');
    expect(lines.length).toBeGreaterThan(1);
    expect(content).toContain('Davis');
  });

  test('csv export respects lastName filter', async ({ page }) => {
    const response = await page.request.get('/owners.csv?lastName=Franklin');

    expect(response.status()).toBe(200);
    let content = await response.text();
    if (content.charCodeAt(0) === 0xfeff) {
      content = content.slice(1);
    }
    expect(content).toContain('Franklin');
  });

  test('csv export with no results returns header only', async ({ page }) => {
    const response = await page.request.get('/owners.csv?lastName=ZZZNoSuchOwner');

    expect(response.status()).toBe(200);
    let content = await response.text();
    if (content.charCodeAt(0) === 0xfeff) {
      content = content.slice(1);
    }

    const lines = content.trim().split(/\r?\n/);
    expect(lines[0]).toContain('Name');
    // Only header row, no data rows
    expect(lines.length).toBe(1);
  });
});
