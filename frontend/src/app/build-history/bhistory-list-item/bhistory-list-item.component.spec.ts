import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BhistoryListItemComponent } from './bhistory-list-item.component';
import * as buildsResponse from 'sample-requests/get.projects.id.branches.ref.builds.response.json';
import { Build } from 'src/app/model/build';

describe('BhistoryListItemComponent', () => {
  let component: BhistoryListItemComponent;
  let fixture: ComponentFixture<BhistoryListItemComponent>;
  const build: Build = buildsResponse['default'][0];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BhistoryListItemComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BhistoryListItemComponent);
    component = fixture.componentInstance;
    component.build = build;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list build info', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const elements: NodeListOf<Element> = nativeElement.querySelectorAll('span');

    expect(elements[3].textContent).toBe('Identifier: <' + build.commit.identifier.substr(0, 7) + '>');
  });
});
