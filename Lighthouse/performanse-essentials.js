const { setTimeout } = require('timers/promises');
const fs = require('fs')
const puppeteer = require('puppeteer')
const lighthouse = require('lighthouse/lighthouse-core/fraggle-rock/api.js')
const testData = require('./testData.json');

const waitTillHTMLRendered = async (page, timeout = 30000) => {
  const checkDurationMsecs = 1000;
  const maxChecks = timeout / checkDurationMsecs;
  let lastHTMLSize = 0;
  let checkCounts = 1;
  let countStableSizeIterations = 0;
  const minStableSizeIterations = 3;

  while(checkCounts++ <= maxChecks){
    let html = await page.content();
    let currentHTMLSize = html.length; 

    let bodyHTMLSize = await page.evaluate(() => document.body.innerHTML.length);

    //console.log('last: ', lastHTMLSize, ' <> curr: ', currentHTMLSize, " body html size: ", bodyHTMLSize);

    if(lastHTMLSize != 0 && currentHTMLSize == lastHTMLSize) 
      countStableSizeIterations++;
    else 
      countStableSizeIterations = 0; //reset the counter

    if(countStableSizeIterations >= minStableSizeIterations) {
      console.log("Fully Rendered Page: " + page.url());
      break;
    }

    lastHTMLSize = currentHTMLSize;
	await setTimeout(checkDurationMsecs);
  }  
};

async function captureReport() {
	//const browser = await puppeteer.launch({args: ['--allow-no-sandbox-job', '--allow-sandbox-debugging', '--no-sandbox', '--disable-gpu', '--disable-gpu-sandbox', '--display', '--ignore-certificate-errors', '--disable-storage-reset=true']});
	const browser = await puppeteer.launch({"headless": true, args: ['--allow-no-sandbox-job', '--allow-sandbox-debugging', '--no-sandbox', '--ignore-certificate-errors', '--disable-storage-reset=true']});
	const page = await browser.newPage();
	const baseURL = "http://localhost/";
	
	await page.setViewport({"width":1920,"height":1080});
	await page.setDefaultTimeout(10000);
	
	const navigationPromise = page.waitForNavigation({timeout: 30000, waitUntil: ['domcontentloaded']});
	await page.goto(baseURL);
    await navigationPromise;
		
	const flow = await lighthouse.startFlow(page, {
		name: 'performance essentials',
		configContext: {
		  settingsOverrides: {
			throttling: {
			  rttMs: 40,
			  throughputKbps: 10240,
			  cpuSlowdownMultiplier: 1,
			  requestLatencyMs: 0,
			  downloadThroughputKbps: 0,
			  uploadThroughputKbps: 0
			},
			throttlingMethod: "simulate",
			screenEmulation: {
			  mobile: false,
			  width: 1920,
			  height: 1080,
			  deviceScaleFactor: 1,
			  disabled: false,
			},
			formFactor: "desktop",
			onlyCategories: ['performance'],
		  },
		},
	});

  	//================================NAVIGATE================================
    await flow.navigate(baseURL, {
		stepName: 'open main page'
		});
  	console.log('main page is opened');
	
	//================================PAGE_ACTIONS================================
	await page.waitForSelector(testData.tablesTab);
	await flow.startTimespan({ stepName: 'Tables tab' });
		await page.click(testData.tablesTab);
        await waitTillHTMLRendered(page);
		await page.waitForSelector(testData.table);
    await flow.endTimespan();
    console.log('Tables tab is open');

	await flow.startTimespan({ stepName: 'Click on a table' });
		await page.click(testData.table);
		await waitTillHTMLRendered(page);
		await page.waitForSelector(testData.addToCart);
	await flow.endTimespan();
	console.log('Click on a table is completed');

	await flow.startTimespan({ stepName: 'Add table to cart' });
		await page.click(testData.addToCart);
		await page.waitForSelector(testData.successInfo);
	await flow.endTimespan();
	console.log('Add table to cart is completed');

	await flow.startTimespan({ stepName: 'Open Cart tab' });
		await page.click(testData.cartTab);
		await waitTillHTMLRendered(page);
		await page.waitForSelector(testData.cartPlaceOrder);
	await flow.endTimespan();
	console.log('Cart tab is open');

	await flow.startTimespan({ stepName: 'Click "Place an order" on Cart Page' });
		await page.click(testData.cartPlaceOrder);
		await page.waitForSelector(testData.infoFormSection);
	await flow.endTimespan();
	console.log('"Place an order" on Cart Page is clicked');

	await flow.startTimespan({ stepName: 'Fill in all required fields, click "Place order"' });
		await page.type(testData.fullNameFiled, testData.username);
		await page.type(testData.addressField, testData.street);
		await page.type(testData.postalCodeField, testData.postalCode);
		await page.type(testData.cityField, testData.city);
		await page.click(testData.countryFIled);
		await page.select(testData.countryFIled, 'AT');
		await page.type(testData.phoneNumberField, testData.phone);
		await page.type(testData.emailField, testData.email);
		await page.click(testData.checkoutPlaceOrder);
		await waitTillHTMLRendered(page);
		await page.waitForSelector(testData.thankYouHeader);
	await flow.endTimespan();
	console.log('"Place an order" on Checkout Page is clicked');

	//================================REPORTING================================
	const reportPath = __dirname + '/user-flow.report.html';

	const report = await flow.generateReport();
	
	fs.writeFileSync(reportPath, report);
	
    await browser.close();
}
captureReport();