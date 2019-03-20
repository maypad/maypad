import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { NotificationComponent } from './notification/notification.component';
import { NotifierContainerComponent } from 'src/testing/notifier-container-stub.component';
import { NotificationContentComponent } from './notification/notification-content/notification-content.component';
import { NotifierService } from 'angular-notifier';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent,
        NavbarComponent,
        NotificationComponent,
        NotifierContainerComponent,
        NotificationContentComponent
      ],
      providers: [{
        provide: NotifierService, useClass: class { show(a, b, c) { } }
      }]
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'MAYPAD'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('MAYPAD');
  });
});
