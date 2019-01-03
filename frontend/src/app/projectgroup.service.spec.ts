import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectgroupService} from './projectgroup.service';
import { TestBed, fakeAsync } from '@angular/core/testing';
import { environment } from '../environments/environment';
import { BuildStatus } from './model/buildStatus';
import { UserServiceAccount, KeyServiceAccount } from './model/serviceAccount';
import * as get_projectgroups_response from '../../sample-requests/get.projectgroups.response.json';
import * as get_projectgroups_id_projects_response from '../../sample-requests/get.projectgroups.id.projects.response.json';
import * as post_projects_response from '../../sample-requests/post.projects.response.json';
import * as post_projects_request from '../../sample-requests/post.projects.request.json';
import * as post_projectgroups_response from '../../sample-requests/post.projectgroups.response.json';
import * as post_projectgroups_request from '../../sample-requests/post.projectgroups.request.json';
import * as put_projectgroups_id_response from '../../sample-requests/put.projectgroups.id.response.json';
import * as put_projectgroups_id_request from '../../sample-requests/put.projectgroups.id.request.json';

describe('Service: ProjectgroupService', () => {
  let service: ProjectgroupService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectgroupService]
    });
    service = TestBed.get(ProjectgroupService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('loads all projectgroups',
    fakeAsync(() => {
      service.loadProjectgroups().subscribe((data) => {
        expect(data.length).toBe(2);

        expect(data[0].id).toEqual(1);
        expect(data[0].name).toEqual('First projectgroup');
        expect(data[0].status).toEqual(BuildStatus.SUCCESS);

        expect(data[1].id).toEqual(2);
        expect(data[1].name).toEqual('Second projectgroup');
        expect(data[1].status).toEqual(BuildStatus.FAILED);
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projectgroups`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projectgroups_response['default']);
    })
  );

  it('loads all projects of a projectgroup',
    fakeAsync(() => {
      service.loadProjects(1).subscribe((data) => {
        expect(data.length).toBe(3);

        expect(data[0].id).toEqual(2);
        expect(data[0].name).toEqual('One Test Project');
        expect(data[0].status).toEqual(BuildStatus.SUCCESS);
        expect(data[0].repositoryURL).toEqual('https://git.maypad.de/one-test-project.git');
        expect(data[0].serviceAccount).toEqual(null);

        expect(data[1].id).toEqual(5);
        expect(data[1].name).toEqual('Another Test Project');
        expect(data[1].status).toEqual(BuildStatus.FAILED);
        expect(data[1].repositoryURL).toEqual('https://git.maypad.de/another-test-project.git');
        expect((<UserServiceAccount>data[1].serviceAccount).username).toEqual('maypad-git');

        expect(data[2].id).toEqual(6);
        expect(data[2].name).toEqual('Yet Another Test Project');
        expect(data[2].status).toEqual(BuildStatus.RUNNING);
        expect(data[2].repositoryURL).toEqual('https://git.maypad.de/yet-another-test-project.git');
        expect((<KeyServiceAccount>data[2].serviceAccount).sshKey).toEqual('djnsvkgfbjsngjkbgjbkluaernl...');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projectgroups/1/projects`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projectgroups_id_projects_response['default']);
    })
  );

  it('should create a project',
    fakeAsync(() => {
      const serviceAccount = new UserServiceAccount();
      serviceAccount.username = 'maypad-git';
      serviceAccount.password = 'ultrageheim';
      service.createProject(2, 'https://git.maypad.de/one-test-project.git', serviceAccount).subscribe((data) => {
        expect(data.id).toEqual(2);
        expect(data.name).toEqual('');
        expect(data.status).toEqual(BuildStatus.UNKNOWN);
        expect(data.repositoryURL).toEqual('https://git.maypad.de/one-test-project.git');
        expect((<UserServiceAccount>data.serviceAccount).username).toEqual('maypad-git');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects`);
      expect(req.request.method).toEqual('POST');
      expect(JSON.parse(JSON.stringify(req.request.body))).toEqual(post_projects_request['default']);
      req.flush(post_projects_response['default']);
    })
  );

  it('should create a projectgroup',
    fakeAsync(() => {
      service.createProjectgroup('New Test Projectgroup').subscribe((data) => {
        expect(data.id).toEqual(42);
        expect(data.name).toEqual('New Test Projectgroup');
        expect(data.status).toEqual(BuildStatus.UNKNOWN);
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projectgroups`);
      expect(req.request.method).toEqual('POST');
      expect(JSON.parse(JSON.stringify(req.request.body))).toEqual(post_projectgroups_request['default']);
      req.flush(post_projectgroups_response['default']);
    })
  );

  it('should update a projectgroup',
    fakeAsync(() => {
      service.updateProjectgroup(42, 'Edited Test Projectgroup').subscribe((data) => {
        expect(data.id).toEqual(42);
        expect(data.name).toEqual('Edited Test Projectgroup');
        expect(data.status).toEqual(BuildStatus.SUCCESS);
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projectgroups/42`);
      expect(req.request.method).toEqual('PUT');
      expect(JSON.parse(JSON.stringify(req.request.body))).toEqual(put_projectgroups_id_request['default']);
      req.flush(put_projectgroups_id_response['default']);
    })
  );

  it('should delete a project',
    fakeAsync(() => {
      service.deleteProject(69);

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/69`);
      expect(req.request.method).toEqual('DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
    })
  );

  it('should delete a projectgroup',
    fakeAsync(() => {
      service.deleteProjectgroup(96);

      const req = httpTestingController.expectOne(`${environment.baseUrl}projectgroups/96`);
      expect(req.request.method).toEqual('DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
    })
  );
});
